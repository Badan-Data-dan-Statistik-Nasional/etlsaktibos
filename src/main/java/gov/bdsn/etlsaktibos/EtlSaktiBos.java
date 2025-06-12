import gov.bdsn.etlsaktibos.entity.Balance;
import gov.bdsn.etlsaktibos.entity.PokBos;
import gov.bdsn.etlsaktibos.entity.PokCombined;
import gov.bdsn.etlsaktibos.entity.PokSakti;
import gov.bdsn.etlsaktibos.util.HibernateUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.h2.tools.Server;
import org.hibernate.query.Query;

String saktiFilePath = "C:/Users/Thahir/Documents/projects/etlsaktibos/SaktiFile.xlsx";
String bosFilePath = "C:/Users/Thahir/Documents/projects/etlsaktibos/BosFile.xlsx";
String outputFilePath="C:/Users/Thahir/Documents/projects/etlsaktibos/Combined.xlsx";

private SessionFactory sessionFactory;
private Session session;

void main (String[] args) throws Exception{

    System.out.println("\n\n" +
            "EtlSaktiBos version 1.0\n" +
            "Copyright (c) Ahmad Thahir (ataherter@yahoo.co.id).\n" +
            "All rights reserved.\n\n");
    Server webServer = Server.createWebServer("-web", "-webPort", "8082").start();

    //println("2896".matches("\\d+"));
    //return;
    //for (int i=0; i < args.length; i++) {
        //if (args[i].equals("-sf")) saktiFilePath=args[i+1];
        //if (args[i].equals("-bf")) bosFilePath=args[i+1];
        //if (args[i].equals("-of")) outputFilePath=args[i+1];
    //}

    sessionFactory = HibernateUtil.getSessionFactory();
    session = sessionFactory.openSession();

    readBosFile(bosFilePath);
    readSaktiFile(saktiFilePath);

    combineBosSakti();
    exportToExcelFile(outputFilePath);

    session.close();
    sessionFactory.close();

    System.out.println("Press Enter to exit....");
    try {
        System.in.read();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    webServer.stop();
}

void exportToExcelFile (String filepath) {
    System.out.println("Export to Excel file ...");
    try {
        FileOutputStream file = new FileOutputStream(filepath);
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheetCombined = workbook.createSheet("Combined");
        String hqlCombined = "FROM PokCombined pc ORDER BY pc.akun, pc.no";
        Query<PokCombined> pokCombinedQuery = session.createQuery(hqlCombined, PokCombined.class);
        List<PokCombined> pokCombineds = pokCombinedQuery.list();

        AtomicInteger rowIndex= new AtomicInteger();
        pokCombineds.forEach(pokCombined -> {
            Row row = sheetCombined.createRow(rowIndex.getAndIncrement());
            if (rowIndex.get()==1) {
                row.createCell(0).setCellValue("ID");
                row.createCell(1).setCellValue("AKUN");
                row.createCell(2).setCellValue("BOSDETAIL");
                row.createCell(3).setCellValue("BOSHARGA");
                row.createCell(4).setCellValue("BOSPAGU");
                row.createCell(5).setCellValue("BOSSATUAN");
                row.createCell(6).setCellValue("BOSVOLUME");
                row.createCell(7).setCellValue("NO");
                row.createCell(8).setCellValue("SAKTIDETAIL");
                row.createCell(9).setCellValue("SAKTIHARGA");
                row.createCell(10).setCellValue("SAKTIPAGU");
                row.createCell(11).setCellValue("SAKTIVOLUME");
                row.createCell(12).setCellValue("STATUS");
            } else {
                row.createCell(0).setCellValue(rowIndex.get()-1);
                //if (pokCombined.getId() != null) row.createCell(0).setCellValue(pokCombined.getId());
                if (pokCombined.getAkun() != null) row.createCell(1).setCellValue(pokCombined.getAkun());
                if (pokCombined.getBosDetail() != null) row.createCell(2).setCellValue(pokCombined.getBosDetail());
                if (pokCombined.getBosHarga() != null) row.createCell(3).setCellValue(pokCombined.getBosHarga());
                if (pokCombined.getBosPagu() != null) row.createCell(4).setCellValue(pokCombined.getBosPagu());
                if (pokCombined.getBosSatuan() != null) row.createCell(5).setCellValue(pokCombined.getBosSatuan());
                if (pokCombined.getBosVolume() != null) row.createCell(6).setCellValue(pokCombined.getBosVolume());
                if (pokCombined.getNo() != null) row.createCell(7).setCellValue(pokCombined.getNo());
                if (pokCombined.getSaktiDetail() != null) row.createCell(8).setCellValue(pokCombined.getSaktiDetail());
                if (pokCombined.getSaktiHarga() != null)row.createCell(9).setCellValue(pokCombined.getSaktiHarga());
                if (pokCombined.getSaktiPagu() != null)row.createCell(10).setCellValue(pokCombined.getSaktiPagu());
                if (pokCombined.getSaktiVolume() != null)row.createCell(11).setCellValue(pokCombined.getSaktiVolume());
                if (pokCombined.getStatus() != null)row.createCell(12).setCellValue(pokCombined.getStatus());
            }
        });

        XSSFSheet sheetCheckBalance = workbook.createSheet("Balance");
        String sqlBalance = "FROM Balance ORDER BY sakun";
        Query<Balance> queryBalance = session.createQuery(sqlBalance, Balance.class);
        List<Balance> balances = queryBalance.list();

        balances.forEach(balance -> {
            // setup cell for sheet Balance here
        });

        workbook.write(file);
        file.close();
        workbook.close();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}

void combineBosSakti () throws InterruptedException {
    System.out.println("Combining ....");
    String query = "INSERT INTO POKCOMBINED (AKUN, NO, BOSDETAIL, BOSHARGA, BOSPAGU, BOSSATUAN, BOSVOLUME, SAKTIDETAIL, SAKTIHARGA, SAKTIPAGU, SAKTIVOLUME, STATUS) " +
            "SELECT  B.AKUN, B.NO, B.DETAIL, B.HARGA, B.PAGU, B.SATUAN, B.VOLUME, S.DETAIL, S.HARGA, S.PAGU, S.VOLUME, " +
            "CASE WHEN S.DETAIL IS NULL THEN 'REMOVE' WHEN (B.HARGA<>S.HARGA OR B.PAGU<>S.PAGU) THEN 'EDIT' ELSE '-' END AS STATUS " +
            "FROM POKBOS B LEFT JOIN POKSAKTI S ON B.AKUN=S.AKUN AND B.DETAIL=S.DETAIL";

    Transaction transaction = session.beginTransaction();
    System.out.println("Combined record count : " +
            session.createNativeQuery(query).executeUpdate());
    transaction.commit();

    query = "INSERT INTO POKCOMBINED (AKUN, SAKTIDETAIL, SAKTIHARGA, SAKTIPAGU, SAKTIVOLUME, STATUS) " +
            "SELECT AKUN, DETAIL, HARGA, PAGU, VOLUME, 'NEW' AS STATUS FROM POKSAKTI WHERE ID NOT IN (SELECT POKSAKTI.ID FROM POKBOS " +
            "LEFT JOIN POKSAKTI " +
            "ON POKBOS.AKUN=POKSAKTI.AKUN AND POKBOS.DETAIL=POKSAKTI.DETAIL " +
            "WHERE POKSAKTI.ID IS NOT NULL)";

    transaction = session.beginTransaction();
    System.out.println("New record count : " +
            session.createNativeQuery(query).executeUpdate());
    transaction.commit();

    query = "SELECT *, CASE WHEN SBP=SSP THEN 'BALANCE' ELSE 'NOT BALANCE' END  AS STATUS FROM (SELECT LEFT(AKUN, 11) AS SAKUN, SUM(BOSPAGU) AS SBP, SUM(SAKTIPAGU) AS SSP " +
            "FROM POKCOMBINED GROUP BY SAKUN)";
    transaction = session.beginTransaction();
    System.out.println("Check balance ...." +
            session.createNativeQuery(query).executeUpdate());
    transaction.commit();
}

void readSaktiFile (String filepath)  {
    System.out.println("Reading sakti file ....");
    try {
        FileInputStream file = new FileInputStream(filepath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);

        String akun = "", akunA="", akunB="", akunC="", akunD="", akunE="", akunF="", akunG="";

        for (Row row : sheet) {

            String a="", no="d", detail="", volume="";
            Integer harga=0, pagu=0;

            a = row.getCell(0).toString();
            no = row.getCell(3).toString();

            if (!a.isBlank()) {
                if (a.length()==9 && a.matches("[0-9]+.[0-9]+.[A-Z]+")) {
                    akunA=a.substring(a.lastIndexOf(".")+1);
                } else if (a.length()==4 && a.matches("[0-9]+")) {
                    akunB=a;
                } else if (a.length()==8 && a.matches("[0-9]+.[A-Z]+")) {
                    akunC=a.substring(a.lastIndexOf(".")+1);
                } else if (a.length()==12 && a.matches("[0-9]+.[A-Z]+.[0-9]+")) {
                    akunD=a.substring(a.lastIndexOf(".")+1);
                } else if (a.length()==3 && a.matches("[0-9]+")) {
                    akunE=a;
                } else if (a.length()==1 && a.matches("[A-Z]")) {
                    akunF=a;
                } else if (a.length()==6 && a.matches("[0-9]+")) {
                    akunG=a;
                }
                akun = akunA + "." + akunB + "." + akunC + "." + akunD + "." + akunE + "." + akunF + "." + akunG;
                //println(a);
            }else if (a.isBlank() && no.equals("  - ")) {
                detail=row.getCell(4).toString();
                volume=row.getCell(6).toString();
                harga=(int) Double.parseDouble(row.getCell(7).toString());
                pagu=(int) Double.parseDouble(row.getCell(9).toString());

                //Save to database
                PokSakti pok = new PokSakti(akun, no, detail, volume, harga, pagu);
                Transaction transaction = session.beginTransaction();
                session.persist(pok);
                transaction.commit();

                //println(pok.getId() + " " + pok.getAkun() + " " + pok.getNo() + " " + pok.getDetail() +
                //        " " + pok.getVolume() + " " + pok.getHarga() + " " + pok.getPagu()) ;
            }
        }
        file.close();
        workbook.close();
    } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
    }
}

void readBosFile (String filepath)  {
    System.out.println("Reading bos file ....");
    try {
        FileInputStream file = new FileInputStream(filepath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);

        String akun ="";
        String a="", b="", c="", d="", e="", f="", g="",
                detail="", satuan="";
        Integer no=0, volume=0, harga=0, pagu=0;

        boolean isFirstRow = true;
        for (Row row : sheet) {
            if (isFirstRow) {
                isFirstRow=false;
                continue;
            }

            if (!row.getCell(0).toString().isEmpty()) {             // a
                a=row.getCell(0).toString();
                continue;
            }
            if (!row.getCell(1).toString().isEmpty()) {             // b
                b=row.getCell(1).toString();
                continue;
            }
            if (!row.getCell(2).toString().isEmpty()) {             // c
                c=row.getCell(2).toString();
                continue;
            }
            if (!row.getCell(3).toString().isEmpty()) {             // d
                d=row.getCell(3).toString();
                continue;
            }
            if (!row.getCell(4).toString().isEmpty()) {             // e
                e=row.getCell(4).toString();
                continue;
            }
            if (!row.getCell(5).toString().isEmpty()) {             // f
                f=row.getCell(5).toString();
                continue;
            }
            if (!row.getCell(6).toString().isEmpty()) {             // g
                g=row.getCell(6).toString();
                continue;
            }

            if (row.getCell(7).getCellType().equals(CellType.NUMERIC)) {             // h
                no = (int) Double.parseDouble(row.getCell(7).toString());
                detail = row.getCell(8).toString();
                volume = (int) Double.parseDouble(row.getCell(9).toString());
                satuan = row.getCell(10).toString();
                harga = (int) Double.parseDouble(row.getCell(11).toString());
                pagu = (int) Double.parseDouble(row.getCell(13).toString());
            }

            akun = a + "." + b + "." + c + "." + d + "." + e + "." + f + "." + g;
            akun = akun.replace("[", "").replace("]", "");

            //Save to database
            PokBos pok = new PokBos(akun, no, detail, volume, satuan, harga, pagu);
            Transaction transaction = session.beginTransaction();
            session.persist(pok);
            transaction.commit();
            //println(pok.getId() + " " + pok.getAkun() + " " + pok.getNo() + " " + pok.getDetail()
            //        + " " + pok.getVolume() + " " + pok.getSatuan() + " " + pok. getHarga() + " " + pok.getPagu());
        }
        file.close();
        workbook.close();
    } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
    }
}