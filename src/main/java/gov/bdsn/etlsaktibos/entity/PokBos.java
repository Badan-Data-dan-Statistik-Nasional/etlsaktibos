package gov.bdsn.etlsaktibos.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class PokBos {
    @Id
    @GeneratedValue
    private Integer id;
    private String  akun;
    private Integer no;
    private String  detail;
    private Integer volume;
    private String  satuan;
    private Integer harga;
    private Integer pagu;

    public PokBos(String akun, Integer no, String detail, Integer volume, String satuan, Integer harga, Integer pagu) {
        this.akun = akun;
        this.no = no;
        this.detail = detail;
        this.volume = volume;
        this.satuan = satuan;
        this.harga = harga;
        this.pagu = pagu;
    }
}
