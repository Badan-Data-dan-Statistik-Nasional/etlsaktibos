package gov.bdsn.etlsaktibos.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class PokSakti {
    @Id
    @GeneratedValue
    private Integer id;
    private String  akun;
    private String  no;
    private String  detail;
    private String  volume;
    private Integer harga;
    private Integer pagu;

    public PokSakti(String akun, String no, String detail, String volume, Integer harga, Integer pagu) {
        this.akun = akun;
        this.no = no;
        this.detail = detail;
        this.volume = volume;
        this.harga = harga;
        this.pagu = pagu;
    }
}
