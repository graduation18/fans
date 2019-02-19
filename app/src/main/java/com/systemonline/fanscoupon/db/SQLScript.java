package com.systemonline.fanscoupon.db;

/**
 * Created by SystemOnline1 on 9/28/2015.
 */
public class SQLScript {

    static String campaignLocationTable = "CREATE TABLE `campaign_location` (\n" +
            "  `branch_id` int(11) NOT NULL ,\n" +
            "  `camp_id` int(11) NOT NULL ,\n" +
            "  `longitude` varchar(30) DEFAULT NULL,\n" +
            "  `latitude` varchar(30) DEFAULT NULL,\n" +
            "  `branch_notify_range` int(10) DEFAULT NULL,\n" +
            "  `coup_id` unsigned int(10)  DEFAULT NULL,\n" +
            "  `coup_slug` varchar(45) DEFAULT NULL,\n" +
            "  `coup_type` varchar(45) DEFAULT NULL,\n" +
            "  `coup_img` varchar(45) DEFAULT NULL,\n" +
            "  `camp_end_date` varchar(45) DEFAULT NULL,\n" +
            "  `camp_name` varchar(45) DEFAULT NULL,\n" +
            "  `camp_title` varchar(45) DEFAULT NULL,\n" +
            "  `camp_body` varchar(45) DEFAULT NULL,\n" +
            "  `frequency` int(4) DEFAULT 0,\n" +
            "  `frequency_limit` int(4) DEFAULT 0,\n" +
            "  `delivered_time` varchar(45) DEFAULT NULL,\n" +
            "  `time_difference` int(4) DEFAULT 0,\n" +
            "  PRIMARY KEY (`branch_id`,`camp_id`)\n" +
            ")\n";

//    static String[] sql = GTables.split("---");
//    static String[] tables = tablesName.split(",");

}
