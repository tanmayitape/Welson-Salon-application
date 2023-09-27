package com.example.welsonsalon;

public class MainPageData {
    String Heading;
    String info;
    int ImageResourceId;

    public static MainPageData[] data ={
            new MainPageData("About Us","Who we are?",R.drawable.about_us_img),
            new MainPageData("Schedule","Our service",R.drawable.service_img),
            new MainPageData("Location","Reach Us",R.drawable.location_img2),
            new MainPageData("Deals","Our Product",R.drawable.deals)
    };
    public MainPageData(String Heading,String info,int ImageResourceId){
        this.Heading = Heading;
       this.info =  info;
       this.ImageResourceId = ImageResourceId;
    }
    public String getHeading(){
        return Heading;
    }
    public String getInfo(){
        return info;
    }
    public int getImageResourceId(){
        return ImageResourceId;
    }

}
