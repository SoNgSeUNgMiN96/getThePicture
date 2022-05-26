package org.tensorflow.lite.examples.detection;


class SampleData {
    private String imgPath;
    private String imgName;

    public SampleData(String imgPath, String imgName){
        this.imgPath = imgPath;
        this.imgName = imgName;
    }

    public String getImg()
    {
        return this.imgPath;
    }

    public String getImgName()
    {
        return this.imgName;
    }
}