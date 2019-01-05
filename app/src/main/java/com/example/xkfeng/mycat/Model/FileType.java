package com.example.xkfeng.mycat.Model;

public enum  FileType {

    document ,
    image ,
    musice ,
    video ,
    apk ,
    other ;

    public static FileType getFileTypeByOridinal(int oridinal){

        for (FileType fileType : values()){
            if (fileType.ordinal() == oridinal){
                return fileType ;
            }
        }

        return document ;
    }
}
