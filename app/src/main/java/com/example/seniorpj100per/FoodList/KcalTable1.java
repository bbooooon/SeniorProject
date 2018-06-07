package com.example.seniorpj100per.FoodList;

/**
 * Created by Smew on 3/11/2560.
 */

public class KcalTable1 {

    private Long protein;

    private Long carbo;

    private Long lipid;

    private String foodname;

    private String filename;

    private String foodname_th;

    private Long energy;

    public Long getProtein() {
        return protein;
    }

    public void setProtein(Long protein) {
        this.protein = protein;
    }

    public Long getCarbo() {
        return carbo;
    }

    public void setCarbo(Long carbo) {
        this.carbo = carbo;
    }

    public Long getLipid() {
        return lipid;
    }

    public void setLipid(Long lipid) {
        this.lipid = lipid;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getEnergy() {
        return energy;
    }

    public void setEnergy(Long energy) {
        this.energy = energy;
    }

    public String getFoodname_th() {
        return foodname_th;
    }

    public void setFoodname_th(String foodname_th) {
        this.foodname_th = foodname_th;
    }

    @Override
    public String toString() {
        return "ClassPojo [protein = " + protein + ", carbo = " + carbo + ", lipid = " + lipid + ", foodname = " + foodname + ", energy = " + energy + "]" + "foodname_th"+ foodname_th;
    }
}
