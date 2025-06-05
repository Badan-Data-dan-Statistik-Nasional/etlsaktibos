import gov.bdsn.etlsaktibos.entity.PokBos;
import gov.bdsn.etlsaktibos.entity.PokSakti;
import gov.bdsn.etlsaktibos.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.h2.tools.Server;

String saktiFilePath = "C:/Users/Thahir/Documents/projects/etlsaktibos/SaktiFile.xlsx";
String bosFilePath = "C:/Users/Thahir/Documents/projects/etlsaktibos/BosFile.xlsx";
String outputFilePath;

private Logger logger = LogManager.getLogger(this.getClass());
private SessionFactory sessionFactory;
private Session session;

void main (String[] args) throws Exception{

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

    session.close();
    sessionFactory.close();

    webServer.stop();
}

void combineBosSakti () throws InterruptedException {
    //Combine the two files here;

    //Just try to get the diff data with a query, and insert the result to PokCombined


    //Start looping in PokBos
    //Insert PokBos record into PokCombined
    //Check if there is the same akun and detail in PokSakti
    //  - if true, insert PokSakti into PokCombined
    //  - if false,

    System.out.println("Press Enter to exit....");
    try {
        System.in.read();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}

void readSaktiFile (String filepath)  {
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

                println(pok.getId() + " " + pok.getAkun() + " " + pok.getNo() + " " + pok.getDetail() +
                        " " + pok.getVolume() + " " + pok.getHarga() + " " + pok.getPagu()) ;
            }
        }
    } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
    }
}

void readBosFile (String filepath)  {
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
            println(pok.getId() + " " + pok.getAkun() + " " + pok.getNo() + " " + pok.getDetail()
                    + " " + pok.getVolume() + " " + pok.getSatuan() + " " + pok. getHarga() + " " + pok.getPagu());
        }
    } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
    }
}