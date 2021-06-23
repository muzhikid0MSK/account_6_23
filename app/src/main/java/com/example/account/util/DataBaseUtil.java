package com.example.account.util;

public class DataBaseUtil {

    public static String DATABASE_NAME ;
    public static Integer DATABASE_VERSION ;

    // TABLE ACCOUNT
    public static final String TABLE_ACCOUNT_NAME = "account";
    public static final String TABLE_ACCOUNT_CREATE = "create table account( id long primary key, user_id long not null, account_type_id long not null, name text not null, remove bit not null);";

    // TABLE ACCOUNT TYPE
    public static final String TABLE_ACCOUNT_TYPE_NAME = "account_type";
    public static final String TABLE_ACCOUNT_TYPE_CREATE = "create table account_type( id long primary key, name text not null, image_url text, remove bit not null default 0);";

    // TABLE EXPENDITURE TYPE
    public static final String TABLE_EXPENDITURE_TYPE_NAME = "expenditure_type";
    public static final String TABLE_EXPENDITURE_TYPE_CREATE = "create table expenditure_type( id long primary key, name text not null, image_url text, rank integer, remove bit not null default 0);";

    // TABLE INCOME TYPE
    public static final String TABLE_INCOME_TYPE_NAME = "income_type";
    public static final String TABLE_INCOME_TYPE_CREATE = "create table income_type( id long primary key, name text not null, image_url text, rank integer, remove bit not null default 0);";

    // TABLE RECORD
    public static final String TABLE_RECORD_NAME = "record";
    public static final String TABLE_RECORD_CREATE = "create table record( id long primary key, account_id long not null, expenditure_type_id long, income_type_id long, amount double not null default 0, remark text, time string not null);";

    // TABLE USER
    public static final String TABLE_USER_NAME = "user";
    public static final String TABLE_USER_CREATE = "create table user( id long primary key, user_name text, phone_number text not null, password text not null, avatar_url text);";

    public static void initDB(String databaseName, Integer databaseVersion){
        DATABASE_NAME = databaseName;
        DATABASE_VERSION = databaseVersion;
    }
}
