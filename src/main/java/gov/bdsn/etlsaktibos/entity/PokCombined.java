package gov.bdsn.etlsaktibos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class PokCombined {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String  akun;
    private Integer no;
    private String  bosDetail;
    private Integer bosVolume;
    private String  bosSatuan;
    private Integer bosHarga;
    private Integer bosPagu;

    private String  saktiDetail;
    private String  saktiVolume;
    private Integer saktiHarga;
    private Integer saktiPagu;

    private String  status;
}
