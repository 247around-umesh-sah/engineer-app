package com.around.engineerbuddy.entity;

import java.util.ArrayList;

public class EOGeoAddressEntity extends BMAObject {


    //     "destination_addresses" : [ "813209, India" ],
//             "origin_addresses" : [ "813208, India" ],
    public ArrayList<String> destination_addresses = new ArrayList<>();
    public ArrayList<String> origin_addresses = new ArrayList<>();
    public ArrayList<EOGepAddressElements> rows = new ArrayList<>();
    //             "rows" : [
//    {
//        "elements" : [
//        {
//            "distance" : {
//            "text" : "28.1 km",
//                    "value" : 28063
//        },
//            "duration" : {
//            "text" : "1 hour 5 mins",
//                    "value" : 3872
//        },
//            "status" : "OK"
//        }
//         ]
//    }
//   ],
    public String status;//" : "OK"
}
