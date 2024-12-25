package com.spider.cpu;

/**
 * @author: javakam
 * @date: 2024-12-24 14:48:04
 */
public class CpuBean {

    /*
     [{"id":"5493","pai_ming":1,"ming_cheng":"AMD Ryzen Threadripper PRO 7995WX","shu_zhi":"158518","bai_fen_bi":100},
     {"id":"5994","pai_ming":2,"ming_cheng":"AMD EPYC 9655P 96-Core","shu_zhi":"155878","bai_fen_bi":98.334573991597}]
     */
    private String id;
    private int paiMing;
    private String mingCheng;
    private String shuZhi;
    private double baiFenBi;

    @Override
    public String toString() {
        return "CpuBean{" +
                "id='" + id + '\'' +
                ", paiMing=" + paiMing +
                ", mingCheng='" + mingCheng + '\'' +
                ", shuZhi='" + shuZhi + '\'' +
                ", baiFenBi=" + baiFenBi +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPaiMing() {
        return paiMing;
    }

    public void setPaiMing(int paiMing) {
        this.paiMing = paiMing;
    }

    public String getMingCheng() {
        return mingCheng;
    }

    public void setMingCheng(String mingCheng) {
        this.mingCheng = mingCheng;
    }

    public String getShuZhi() {
        return shuZhi;
    }

    public void setShuZhi(String shuZhi) {
        this.shuZhi = shuZhi;
    }

    public double getBaiFenBi() {
        return baiFenBi;
    }

    public void setBaiFenBi(double baiFenBi) {
        this.baiFenBi = baiFenBi;
    }
}