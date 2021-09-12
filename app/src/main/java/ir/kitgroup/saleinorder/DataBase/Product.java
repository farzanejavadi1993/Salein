package ir.kitgroup.saleinorder.DataBase;



import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import ir.kitgroup.saleinorder.Util.Util;

public class Product extends SugarRecord {
    @Unique
    public String I;
    public String PID1;
    public String PID2;
    public String PID3;
    public String N;//name
    public String NIP;//name KHOLASE
    public Double PU1;
    public Double PU2;
    public Double PU3;
    public Double PU4;
    public Double PU5;
    public String DES;
    public Boolean TAX;
    public Boolean STS;
    public Double PERC_DIS;
    public String IMG;
    public String descItem;

    public int MS;//MAX

    public Boolean getSTS() {
      return STS;

    }

    public void setSTS(Boolean STS) {
        this.STS = STS;
    }

    public Boolean star;

    public String getPRDUID() {
        return I;
    }
    public String getPRDLVLUID2() {
        return PID2;
    }
    public String getPRDNAME() {
        return N;
    }
    public Double getPRDPRICEPERUNIT1() {
        Double showPrice = 0.0;
        try {

            String priceList = Util.getPrice();

            switch (priceList) {
                case "2":
                    showPrice = PU2;
                    break;
                case "3":
                    showPrice = PU3;
                    break;
                case "4":
                    showPrice = PU4;
                    break;
                case "5":
                    showPrice = PU5;
                    break;
                default:
                    showPrice = PU1;
                    break;
            }



        } catch (Exception ignored) {
        }
        return showPrice;
    }
    public Double getPRDPERCENTDISCOUNT() {
        return PERC_DIS;
    }

    public Double AMOUNT;

    public Boolean getStar() {
        return star;
    }

    public void setStar(Boolean star) {
        this.star = star;
    }

    public Double getAmount() {
        if (AMOUNT==null) {
            return 0.0;
        } else {
            return AMOUNT;
        }

    }

    public void setAmount(Double amount1) {

        this.AMOUNT = amount1;
    }

    public Product(){}

}
