package vn.co.taxinet.mobile.database;

import java.util.ArrayList;
import java.util.List;

import vn.co.taxinet.mobile.model.Company;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.Payment;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.model.PromotionTripRiders;
import vn.co.taxinet.mobile.model.Rider;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripRiderStatus;
import vn.co.taxinet.mobile.utils.Constants.RiderType;
import vn.co.taxinet.mobile.utils.Utils;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 10;

	// Database Name
	private static final String DATABASE_NAME = "TaxiNet";

	// Table Names
	private static final String TABLE_DRIVER = "TABLE_DRIVER";
	private static final String TABLE_COMPANY_INFO = "TABLE_COMPANY_INFO";
	private static final String TABLE_TRIP = "TABLE_TRIP";
	private static final String TABLE_RIDER = "TABLE_RIDER";
	private static final String TABLE_PROMOTION_TRIP = "TABLE_PROMOTION_TRIP";
	private static final String TABLE_PROMOTION_TRIP_RIDER = "TABLE_PROMOTION_TRIP_RDIER";
	private static final String TABLE_PAYMENT = "TABLE_PAYMENT";

	// DRIVER Table - column names
	private static final String COLUMN_DRIVER_ID = "COLUMN_DRIVER_ID";
	private static final String COLUMN_DRIVER_IMAGES = "COLUMN_DRIVER_IMAGES";
	private static final String COLUMN_DRIVER_FIRST_NAME = "COLUMN_DRIVER_FIRST_NAME";
	private static final String COLUMN_DRIVER_LAST_NAME = "COLUMN_DRIVER_LAST_NAME";
	private static final String COLUMN_DRIVER_EMAIL = "COLUMN_DRIVER_EMAIL";
	private static final String COLUMN_DRIVER_PASSWORD = "COLUMN_DRIVER_PASSWORD";
	private static final String COLUMN_DRIVER_PHONE_NUMBER = "COLUMN_DRIVER_PHONE_NUMBER";
	private static final String COLUMN_DRIVER_BALANCE = "COLUMN_DRIVER_BALANCE";
	private static final String COLUMN_DRIVER_HOME_ADDRESS = "COLUMN_DRIVER_HOME_ADDRESS";

	// private static final String COLUMN_TERM_ID = "COLUMN_TERM_ID";
	// private static final String COLUMN_TERM_CONTENT = "COLUMN_TERM_CONTENT";

	// CompanyInfo Table - column names
	private static final String COLUMN_COMPANY_ID = "COLUMN_COMPANY_ID";
	private static final String COLUMN_COMPANY_NAME = "COLUMN_COMPANY_NAME";
	private static final String COLUMN_COMPANY_ADDRESS = "COLUMN_COMPANY_ADDRESS";
	private static final String COLUMN_COMPANY_CITY = "COLUMN_COMPANY_CITY";
	private static final String COLUMN_COMPANY_POSTAL_CODE = "COLUMN_COMPANY_POSTAL_CODE";
	private static final String COLUMN_COMPANY_PHONE = "COLUMN_COMPANY_PHONE";
	private static final String COLUMN_COMPANY_VAT_NUMBER = "COLUMN_COMPANY_VAT_NUMBER";
	// Trip Table - column names
	private static final String COLUMN_TRIP_ID = "COLUMN_TRIP_ID";
	private static final String COLUMN_TRIP_START_LATITUDE = "COLUMN_TRIP_START_LATITUDE";
	private static final String COLUMN_TRIP_START_LONGITUDE = "COLUMN_TRIP_START_LONGITUDE";
	private static final String COLUMN_TRIP_STOP_LATITUDE = "COLUMN_TRIP_STOP_LATITUDE";
	private static final String COLUMN_TRIP_STOP_LONGITUDE = "COLUMN_TRIP_STOP_LONGITUDE";
	private static final String COLUMN_TRIP_STATUS = "COLUMN_TRIP_STATUS";
	private static final String COLUMN_TRIP_FEE = "COLUMN_TRIP_FEE";
	private static final String COLUMN_TRIP_PAYMENT_METHOD = "COLUMN_TRIP_PAYMENT_METHOD";
	private static final String COLUMN_TRIP_COMPLETION_TIME = "COLUMN_TRIP_COMPLETION_TIME";
	private static final String COLUMN_TRIP_START_TIME = "COLUMN_TRIP_START_TIME";
	private static final String COLUMN_TRIP_DISTANCE = "COLUMN_TRIP_REAL_DISTANCE";
	private static final String COLUMN_TRIP_FROM_ADDRESS = "COLUMN_TRIP_FROM_ADDRESS";
	private static final String COLUMN_TRIP_TO_ADDRESS = "COLUMN_TRIP_TO_ADDRESS";
	private static final String COLUMN_TRIP_RIDER_ID = "COLUMN_TRIP_RIDER_ID";

	// Rider Table - column names
	private static final String COLUMN_RIDER_ID = "COLUMN_RIDER_ID";
	private static final String COLUMN_RIDER_FIRST_NAME = "COLUMN_RIDER_FIRST_NAME";
	private static final String COLUMN_RIDER_LAST_NAME = "COLUMN_RIDER_LAST_NAME";
	private static final String COLUMN_RIDER_PHONE = "COLUMN_RIDER_PHONE";
	private static final String COLUMN_RIDER_IMAGE = "COLUMN_RIDER_IMAGE";
	private static final String COLUMN_RIDER_TYPE = "COLUMN_RIDER_TYPE";

	// Promotion Trip Table - column names
	private static final String COLUMN_PROMOTION_TRIP_ID = "COLUMN_PROMOTION_TRIP_ID";
	private static final String COLUMN_PROMOTION_TRIP_FROM_CITY = "COLUMN_PROMOTION_TRIP_FROM_CITY";
	private static final String COLUMN_PROMOTION_TRIP_FROM_ADDRESS = "COLUMN_PROMOTION_TRIP_FROM_ADDRESS";
	private static final String COLUMN_PROMOTION_TRIP_TO_CITY = "COLUMN_PROMOTION_TRIP_TO_CITY";
	private static final String COLUMN_PROMOTION_TRIP_TO_ADDRESS = "COLUMN_PROMOTION_TRIP_TO_ADDRESS";
	private static final String COLUMN_PROMOTION_TRIP_TIME = "COLUMN_PROMOTION_TRIP_TIME";
	private static final String COLUMN_PROMOTION_TRIP_FEE = "COLUMN_PROMOTION_TRIP_FEE";
	private static final String COLUMN_PROMOTION_TRIP_START_LATITUDE = "COLUMN_PROMOTION_TRIP_START_LATITUDE";
	private static final String COLUMN_PROMOTION_TRIP_START_LONGITUDE = "COLUMN_PROMOTION_TRIP_START_LONGITUDE";
	private static final String COLUMN_PROMOTION_TRIP_STOP_LATITUDE = "COLUMN_PROMOTION_TRIP_STOP_LATITUDE";
	private static final String COLUMN_PROMOTION_TRIP_STOP_LONGITUDE = "COLUMN_PROMOTION_TRIP_STOP_LONGITUDE";
	private static final String COLUMN_PROMOTION_TRIP_STATUS = "COLUMN_PROMOTION_TRIP_STATUS";

	// Payment table
	private static final String COLUMN_PAYMENT_ID = "COLUMN_PAYMENT_ID";
	private static final String COLUMN_PAYMENT_EXPIRED_MONTH = "COLUMN_PAYMENT_EXPIRED_MONTH";
	private static final String COLUMN_PAYMENT_EXPIRED_YEAR = "COLUMN_PAYMENT_EXPIRED_YEAR";
	private static final String COLUMN_PAYMENT_CVV = "COLUMN_PAYMENT_CVV";
	private static final String COLUMN_PAYMENT_CARD_NUMBER = "COLUMN_PAYMENT_CARD_NUMBER";
	private static final String COLUMN_PAYMENT_BANK_NAME = "COLUMN_PAYMENT_BANK_NAME";
	private static final String COLUMN_PAYMENT_TYPE = "COLUMN_PAYMENT_TYPE";

	// Payment table
	private static final String COLUMN_PROMOTION_TRIP_RIDER_FROM_CITY = "COLUMN_PROMOTION_TRIP_RIDER_FROM_CITY";
	private static final String COLUMN_PROMOTION_TRIP_RIDER_TO_CITY = "COLUMN_PROMOTION_TRIP_RIDER_TO_CITY";
	private static final String COLUMN_PROMOTION_TRIP_RIDER_FROM_ADDRESS = "COLUMN_PROMOTION_TRIP_RIDER_FROM_ADDRESS";
	private static final String COLUMN_PROMOTION_TRIP_RIDER_TO_ADDRESS = "COLUMN_PROMOTION_TRIP_RIDER_TO_ADDRESS";
	private static final String COLUMN_PROMOTION_TRIP_RIDER_NUMER_OF_SEATS = "COLUMN_PROMOTION_TRIP_RIDER_NUMER_OF_SEALS";
	private static final String COLUMN_PROMOTION_TRIP_RIDER_STATUS = "COLUMN_PROMOTION_TRIP_RIDER_STATUS";
	private static final String COLUMN_PROMOTION_TRIP_RIDER_TIME = "COLUMN_PROMOTION_TRIP_RIDER_TIME";
	private static final String COLUMN_PROMOTION_TRIP_RIDER_RIDER_ID = "COLUMN_PROMOTION_TRIP_RIDER_RIDER_ID";

	// DRIVER table create statement
	private static final String CREATE_TABLE_DRIVER = "CREATE TABLE "
			+ TABLE_DRIVER + "(" + COLUMN_DRIVER_ID + " TEXT PRIMARY KEY,"
			+ COLUMN_DRIVER_IMAGES + " TEXT," + COLUMN_DRIVER_FIRST_NAME
			+ " TEXT," + COLUMN_DRIVER_LAST_NAME + " TEXT,"
			+ COLUMN_DRIVER_EMAIL + " TEXT," + COLUMN_DRIVER_BALANCE + " TEXT,"
			+ COLUMN_DRIVER_PASSWORD + " TEXT," + COLUMN_DRIVER_HOME_ADDRESS
			+ " TEXT," + COLUMN_DRIVER_PHONE_NUMBER + " TEXT" + ")";

	// TRIP table create statement
	private static final String CREATE_TABLE_TRIP = "CREATE TABLE "
			+ TABLE_TRIP + "(" + COLUMN_TRIP_ID + " TEXT PRIMARY KEY,"
			+ COLUMN_TRIP_FEE + " TEXT," + COLUMN_TRIP_PAYMENT_METHOD
			+ " TEXT," + COLUMN_TRIP_START_LATITUDE + " TEXT,"
			+ COLUMN_TRIP_START_LONGITUDE + " TEXT,"
			+ COLUMN_TRIP_STOP_LATITUDE + " TEXT," + COLUMN_TRIP_STOP_LONGITUDE
			+ " TEXT," + COLUMN_TRIP_COMPLETION_TIME + " TEXT,"
			+ COLUMN_TRIP_RIDER_ID + " TEXT," + COLUMN_TRIP_FROM_ADDRESS
			+ " TEXT," + COLUMN_TRIP_TO_ADDRESS + " TEXT,"
			+ COLUMN_TRIP_DISTANCE + " TEXT," + COLUMN_TRIP_START_TIME
			+ " TEXT," + COLUMN_TRIP_STATUS + " TEXT" + ")";

	// COMPANY INFO table create statement
	private static final String CREATE_TABLE_COMPANY_INFO = "CREATE TABLE "
			+ TABLE_COMPANY_INFO + "(" + COLUMN_COMPANY_ID
			+ " TEXT PRIMARY KEY," + COLUMN_COMPANY_NAME + " TEXT,"
			+ COLUMN_COMPANY_ADDRESS + " TEXT," + COLUMN_COMPANY_CITY
			+ " TEXT," + COLUMN_COMPANY_POSTAL_CODE + " TEXT,"
			+ COLUMN_COMPANY_PHONE + " TEXT," + COLUMN_COMPANY_VAT_NUMBER
			+ " TEXT" + ")";

	// VIP RIDER table create statement
	private static final String CREATE_TABLE_RIDER = "CREATE TABLE "
			+ TABLE_RIDER + "(" + COLUMN_RIDER_ID + " TEXT PRIMARY KEY,"
			+ COLUMN_RIDER_FIRST_NAME + " TEXT," + COLUMN_RIDER_LAST_NAME
			+ " TEXT," + COLUMN_RIDER_TYPE + " TEXT," + COLUMN_RIDER_IMAGE
			+ " TEXT," + COLUMN_RIDER_PHONE + " TEXT" + ")";
	// VIP RIDER table create statement
	private static final String CREATE_TABLE_PROMOTION_TRIP = "CREATE TABLE "
			+ TABLE_PROMOTION_TRIP + "(" + COLUMN_PROMOTION_TRIP_ID
			+ " TEXT PRIMARY KEY," + COLUMN_PROMOTION_TRIP_FROM_CITY + " TEXT,"
			+ COLUMN_PROMOTION_TRIP_FROM_ADDRESS + " TEXT,"
			+ COLUMN_PROMOTION_TRIP_TO_CITY + " TEXT,"
			+ COLUMN_PROMOTION_TRIP_TO_ADDRESS + " TEXT,"
			+ COLUMN_PROMOTION_TRIP_TIME + " TEXT," + COLUMN_PROMOTION_TRIP_FEE
			+ " TEXT," + COLUMN_PROMOTION_TRIP_START_LATITUDE + " TEXT,"
			+ COLUMN_PROMOTION_TRIP_START_LONGITUDE + " TEXT,"
			+ COLUMN_PROMOTION_TRIP_STOP_LATITUDE + " TEXT,"
			+ COLUMN_PROMOTION_TRIP_STOP_LONGITUDE + " TEXT,"
			+ COLUMN_PROMOTION_TRIP_STATUS + " TEXT" + ")";

	// VIP RIDER table create statement
	private static final String CREATE_TABLE_PROMOTION_TRIP_RIDER = "CREATE TABLE "
			+ TABLE_PROMOTION_TRIP_RIDER
			+ "("
			+ COLUMN_PROMOTION_TRIP_RIDER_RIDER_ID
			+ " TEXT ,"
			+ COLUMN_PROMOTION_TRIP_RIDER_FROM_ADDRESS
			+ " TEXT,"
			+ COLUMN_PROMOTION_TRIP_RIDER_TO_ADDRESS
			+ " TEXT,"
			+ COLUMN_PROMOTION_TRIP_RIDER_FROM_CITY
			+ " TEXT,"
			+ COLUMN_PROMOTION_TRIP_RIDER_TO_CITY
			+ " TEXT,"
			+ COLUMN_PROMOTION_TRIP_RIDER_NUMER_OF_SEATS
			+ " TEXT,"
			+ COLUMN_PROMOTION_TRIP_RIDER_STATUS
			+ " TEXT,"
			+ COLUMN_PROMOTION_TRIP_ID
			+ " TEXT,"
			+ COLUMN_PROMOTION_TRIP_RIDER_TIME
			+ " TEXT, PRIMARY KEY ("
			+ COLUMN_PROMOTION_TRIP_ID
			+ ","
			+ COLUMN_PROMOTION_TRIP_RIDER_RIDER_ID + ") )";

	// Payment table create statement
	private static final String CREATE_TABLE_PAYMENT = "CREATE TABLE "
			+ TABLE_PAYMENT + "(" + COLUMN_PAYMENT_ID + " INTEGER PRIMARY KEY,"
			+ COLUMN_PAYMENT_BANK_NAME + " TEXT," + COLUMN_PAYMENT_TYPE
			+ " TEXT," + COLUMN_PAYMENT_CARD_NUMBER + " TEXT,"
			+ COLUMN_PAYMENT_CVV + " TEXT," + COLUMN_PAYMENT_EXPIRED_MONTH
			+ " TEXT," + COLUMN_PAYMENT_EXPIRED_YEAR + " TEXT" + ")";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_DRIVER);
		db.execSQL(CREATE_TABLE_COMPANY_INFO);
		db.execSQL(CREATE_TABLE_TRIP);
		db.execSQL(CREATE_TABLE_RIDER);
		db.execSQL(CREATE_TABLE_PROMOTION_TRIP);
		db.execSQL(CREATE_TABLE_PROMOTION_TRIP_RIDER);
		db.execSQL(CREATE_TABLE_PAYMENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANY_INFO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROMOTION_TRIP);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROMOTION_TRIP_RIDER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT);
		onCreate(db);
	}

	public void addDriver(Driver driver) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(COLUMN_DRIVER_ID, driver.getId());
			values.put(COLUMN_DRIVER_FIRST_NAME, driver.getFirstName());
			values.put(COLUMN_DRIVER_LAST_NAME, driver.getLastName());
			values.put(COLUMN_DRIVER_IMAGES, driver.getImage());
			values.put(COLUMN_DRIVER_EMAIL, driver.getEmail());
			values.put(COLUMN_DRIVER_PHONE_NUMBER, driver.getPhoneNumber());
			values.put(COLUMN_DRIVER_PASSWORD, driver.getPassword());
			values.put(COLUMN_DRIVER_BALANCE, driver.getBalance());
			values.put(COLUMN_DRIVER_HOME_ADDRESS, driver.getHomeAddress());
			db.insertOrThrow(TABLE_DRIVER, null, values);
		} catch (Exception ex) {
		}
		closeDatabse();
	}

	public void closeDatabse() {
		SQLiteDatabase db = getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/*
	 * find a driver
	 */
	public Driver findDriver() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_DRIVER;
		Cursor c = db.rawQuery(selectQuery, null);
		Driver driver = null;
		if (c.moveToFirst()) {
			driver = new Driver();
			driver.setId(c.getString(c.getColumnIndex(COLUMN_DRIVER_ID)));
			driver.setFirstName((c.getString(c
					.getColumnIndex(COLUMN_DRIVER_FIRST_NAME))));
			driver.setLastName(c.getString(c
					.getColumnIndex(COLUMN_DRIVER_LAST_NAME)));
			driver.setImage(c.getString(c.getColumnIndex(COLUMN_DRIVER_IMAGES)));
			driver.setEmail(c.getString(c.getColumnIndex(COLUMN_DRIVER_EMAIL)));
			driver.setPhoneNumber(c.getString(c
					.getColumnIndex(COLUMN_DRIVER_PHONE_NUMBER)));
			driver.setPassword(c.getString(c
					.getColumnIndex(COLUMN_DRIVER_PASSWORD)));
			driver.setBalance(c.getDouble(c
					.getColumnIndex(COLUMN_DRIVER_BALANCE)));
			driver.setHomeAddress(c.getString(c
					.getColumnIndex(COLUMN_DRIVER_HOME_ADDRESS)));
		}
		closeDatabse();
		return driver;
	}

	/*
	 * Updating a driver
	 */
	public void updateDriver(Driver driver) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_DRIVER_FIRST_NAME, driver.getFirstName());
		values.put(COLUMN_DRIVER_LAST_NAME, driver.getLastName());
		values.put(COLUMN_DRIVER_IMAGES, driver.getImage());
		values.put(COLUMN_DRIVER_EMAIL, driver.getEmail());
		values.put(COLUMN_DRIVER_PHONE_NUMBER, driver.getPhoneNumber());
		values.put(COLUMN_DRIVER_PASSWORD, driver.getPassword());
		values.put(COLUMN_DRIVER_HOME_ADDRESS, driver.getHomeAddress());
		db.update(TABLE_DRIVER, values, COLUMN_DRIVER_ID + " = ?",
				new String[] { String.valueOf(driver.getId()) });
		closeDatabse();
	}

	/*
	 * Deleting a driver
	 */
	public void deleteDriver() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_DRIVER, "1", null);
		closeDatabse();

	}

	public void deleteAllData() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_DRIVER, "1", null);
		db.delete(TABLE_COMPANY_INFO, "1", null);
		// db.delete(TABLE_TERM, "1", null);
		db.delete(TABLE_TRIP, "1", null);
		db.delete(TABLE_RIDER, "1", null);
		db.delete(TABLE_PAYMENT, "1", null);
		db.delete(TABLE_PROMOTION_TRIP, "1", null);
		db.delete(TABLE_PROMOTION_TRIP_RIDER, "1", null);

		closeDatabse();

	}

	public ArrayList<Driver> getListDriver() {
		ArrayList<Driver> listDrivers = new ArrayList<Driver>();
		String selectQuery = "SELECT  * FROM " + TABLE_DRIVER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Driver driver = new Driver();
				driver.setId(c.getString(c.getColumnIndex(COLUMN_DRIVER_ID)));
				driver.setFirstName((c.getString(c
						.getColumnIndex(COLUMN_DRIVER_FIRST_NAME))));
				driver.setLastName(c.getString(c
						.getColumnIndex(COLUMN_DRIVER_LAST_NAME)));
				driver.setImage(c.getString(c
						.getColumnIndex(COLUMN_DRIVER_IMAGES)));
				driver.setEmail(c.getString(c
						.getColumnIndex(COLUMN_DRIVER_EMAIL)));
				driver.setPhoneNumber(c.getString(c
						.getColumnIndex(COLUMN_DRIVER_PHONE_NUMBER)));
				driver.setPassword(c.getString(c
						.getColumnIndex(COLUMN_DRIVER_PASSWORD)));
				listDrivers.add(driver);
			} while (c.moveToNext());
		}

		closeDatabse();

		return listDrivers;
	}

	public void addCompany(Company company) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_COMPANY_ID, company.getId());
		values.put(COLUMN_COMPANY_NAME, company.getName());
		values.put(COLUMN_COMPANY_ADDRESS, company.getAddress());
		values.put(COLUMN_COMPANY_CITY, company.getCity());
		values.put(COLUMN_COMPANY_PHONE, company.getPhone());
		values.put(COLUMN_COMPANY_POSTAL_CODE, company.getPostalCode());
		values.put(COLUMN_COMPANY_VAT_NUMBER, company.getVatNumeber());
		db.insert(TABLE_COMPANY_INFO, null, values);
		closeDatabse();
	}

	public Company findCompany() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_COMPANY_INFO;
		Cursor c = db.rawQuery(selectQuery, null);
		Company company = null;
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			company = new Company();
			company.setName(c.getString(c.getColumnIndex(COLUMN_COMPANY_NAME)));
			company.setAddress(c.getString(c
					.getColumnIndex(COLUMN_COMPANY_ADDRESS)));
			company.setCity(c.getString(c.getColumnIndex(COLUMN_COMPANY_CITY)));
			company.setPhone(c.getString(c.getColumnIndex(COLUMN_COMPANY_PHONE)));
			company.setPostalCode(c.getString(c
					.getColumnIndex(COLUMN_COMPANY_POSTAL_CODE)));
			company.setVatNumeber(c.getString(c
					.getColumnIndex(COLUMN_COMPANY_VAT_NUMBER)));
		}
		closeDatabse();
		return company;
	}

	public void deleteCompany() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_COMPANY_INFO);
		closeDatabse();
	}

	public void deleteTrip() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_TRIP);
		closeDatabse();
	}

	public void deleteCompleteTrip() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_TRIP + " WHERE " + COLUMN_TRIP_STATUS
				+ "='" + TripStatus.COMPLETED + "'");
		closeDatabse();
	}

	public Trip getTrip() {
		String selectQuery = "SELECT  * FROM " + TABLE_TRIP + " WHERE "
				+ COLUMN_TRIP_STATUS + " != 'CA' AND " + COLUMN_TRIP_STATUS
				+ " != 'TC' ";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		Trip trip = null;
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			trip = new Trip();
			trip.setTripId(c.getString(c.getColumnIndex(COLUMN_TRIP_FEE)));
			trip.setFee(Float.parseFloat(c.getString(c
					.getColumnIndex(COLUMN_TRIP_FEE))));
			trip.setDistance(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_DISTANCE))));
			trip.setPaymenMethod(c.getString(c
					.getColumnIndex(COLUMN_TRIP_PAYMENT_METHOD)));
			trip.setFromLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_START_LATITUDE))));
			trip.setFromLongitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_START_LONGITUDE))));
			trip.setStatus(c.getString(c.getColumnIndex(COLUMN_TRIP_STATUS)));
			trip.setToLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_STOP_LATITUDE))));
			trip.setToLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_STOP_LONGITUDE))));
			trip.setTripId(c.getString(c.getColumnIndex(COLUMN_TRIP_ID)));
			Rider rider = new Rider();
			rider.setId(c.getString(c.getColumnIndex(COLUMN_TRIP_RIDER_ID)));
			trip.setRider(rider);
		}
		closeDatabse();
		return trip;
	}

	public List<Trip> getListTrip() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<Trip> list = new ArrayList<Trip>();
		Trip trip = new Trip();
		String listTodayTrip = "SELECT  * FROM " + TABLE_TRIP + " WHERE "
				+ COLUMN_TRIP_STATUS + " = 'TC' AND ("
				+ COLUMN_TRIP_COMPLETION_TIME + " <= '" + Utils.getDate()
				+ " 23:59:59' AND " + COLUMN_TRIP_COMPLETION_TIME + " >= '"
				+ Utils.getDate() + " 00:00:00')";
		String listYesterdayTrip = "SELECT  * FROM " + TABLE_TRIP + " WHERE "
				+ COLUMN_TRIP_STATUS + " = 'TC' AND ("
				+ COLUMN_TRIP_COMPLETION_TIME + " <= '"
				+ Utils.getYesterdayDateString() + " 23:59:59' AND "
				+ COLUMN_TRIP_COMPLETION_TIME + " >= '"
				+ Utils.getYesterdayDateString() + " 00:00:00')";

		Cursor c1 = db.rawQuery(listTodayTrip, null);

		// looping through all rows and adding to list
		if (c1.moveToFirst()) {
			trip = new Trip();
			trip.setCompletionTime(Utils.getDate());
			trip.setTripId("null");
			list.add(trip);
			do {
				trip = new Trip();
				trip.setTripId(c1.getString(c1.getColumnIndex(COLUMN_TRIP_FEE)));
				trip.setFromLatitude(Double.parseDouble(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_START_LATITUDE))));
				trip.setFromLongitude(Double.parseDouble(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_START_LONGITUDE))));
				trip.setToLatitude(Double.parseDouble(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_STOP_LATITUDE))));
				trip.setToLatitude(Double.parseDouble(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_STOP_LONGITUDE))));
				trip.setTripId(c1.getString(c1.getColumnIndex(COLUMN_TRIP_ID)));
				trip.setPaymenMethod(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_PAYMENT_METHOD)));
				trip.setCompletionTime(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_COMPLETION_TIME)));
				trip.setDistance(Double.parseDouble(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_DISTANCE))));
				trip.setFromAddress(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_FROM_ADDRESS)));
				trip.setToAddress(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_TO_ADDRESS)));
				trip.setFee(c1.getDouble(c1.getColumnIndex(COLUMN_TRIP_FEE)));
				trip.setStartTime(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_START_TIME)));
				String riderId = c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_RIDER_ID));
				Rider rider = findRiderById(riderId);
				trip.setRider(rider);
				list.add(trip);
			} while (c1.moveToNext());
		}

		db = this.getReadableDatabase();
		Cursor c = db.rawQuery(listYesterdayTrip, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			trip = new Trip();
			trip.setCompletionTime(Utils.getYesterdayDateString());
			trip.setTripId("null");
			list.add(trip);
			do {
				trip = new Trip();
				trip.setTripId(c.getString(c.getColumnIndex(COLUMN_TRIP_FEE)));
				trip.setFromLatitude(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_TRIP_START_LATITUDE))));
				trip.setFromLongitude(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_TRIP_START_LONGITUDE))));
				trip.setToLatitude(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_TRIP_STOP_LATITUDE))));
				trip.setToLatitude(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_TRIP_STOP_LONGITUDE))));
				trip.setTripId(c.getString(c.getColumnIndex(COLUMN_TRIP_ID)));
				trip.setPaymenMethod(c.getString(c
						.getColumnIndex(COLUMN_TRIP_PAYMENT_METHOD)));
				trip.setCompletionTime(c.getString(c
						.getColumnIndex(COLUMN_TRIP_COMPLETION_TIME)));
				trip.setDistance(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_TRIP_DISTANCE))));
				trip.setFromAddress(c.getString(c
						.getColumnIndex(COLUMN_TRIP_FROM_ADDRESS)));
				trip.setToAddress(c.getString(c
						.getColumnIndex(COLUMN_TRIP_TO_ADDRESS)));
				trip.setFee(c.getDouble(c.getColumnIndex(COLUMN_TRIP_FEE)));
				trip.setStartTime(c.getString(c
						.getColumnIndex(COLUMN_TRIP_START_TIME)));
				String riderId = c.getString(c
						.getColumnIndex(COLUMN_TRIP_RIDER_ID));
				Rider rider = findRiderById(riderId);
				trip.setRider(rider);
				list.add(trip);
			} while (c.moveToNext());
		}
		closeDatabse();
		return list;
	}

	public Trip findTripById(String id) {
		String selectQuery = "SELECT  * FROM " + TABLE_TRIP + " WHERE "
				+ COLUMN_TRIP_ID + " = '" + id + "'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		Trip trip = null;
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			trip = new Trip();
			trip.setTripId(c.getString(c.getColumnIndex(COLUMN_TRIP_ID)));
			trip.setFee(Float.parseFloat(c.getString(c
					.getColumnIndex(COLUMN_TRIP_FEE))));
			trip.setDistance(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_DISTANCE))));
			trip.setPaymenMethod(c.getString(c
					.getColumnIndex(COLUMN_TRIP_PAYMENT_METHOD)));
			trip.setFromLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_START_LATITUDE))));
			trip.setFromLongitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_START_LONGITUDE))));
			trip.setStatus(c.getString(c.getColumnIndex(COLUMN_TRIP_STATUS)));
			trip.setToLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_STOP_LATITUDE))));
			trip.setToLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_STOP_LONGITUDE))));
			Rider rider = new Rider();
			rider.setId(c.getString(c.getColumnIndex(COLUMN_TRIP_RIDER_ID)));
			trip.setRider(rider);
		}
		closeDatabse();
		return trip;
	}

	public void addTrip(Trip trip) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_TRIP_ID, trip.getTripId());
		values.put(COLUMN_TRIP_FEE, trip.getFee());
		values.put(COLUMN_TRIP_DISTANCE, trip.getDistance());
		values.put(COLUMN_TRIP_PAYMENT_METHOD, trip.getPaymenMethod());
		values.put(COLUMN_TRIP_START_LATITUDE, trip.getFromLatitude());
		values.put(COLUMN_TRIP_START_LONGITUDE, trip.getFromLongitude());
		values.put(COLUMN_TRIP_STATUS, trip.getStatus());
		values.put(COLUMN_TRIP_STOP_LATITUDE, trip.getToLatitude());
		values.put(COLUMN_TRIP_STOP_LONGITUDE, trip.getToLatitude());
		values.put(COLUMN_TRIP_COMPLETION_TIME, trip.getCompletionTime());
		values.put(COLUMN_TRIP_FROM_ADDRESS, trip.getFromAddress());
		values.put(COLUMN_TRIP_TO_ADDRESS, trip.getToAddress());
		values.put(COLUMN_TRIP_RIDER_ID, trip.getRider().getId());
		values.put(COLUMN_TRIP_START_TIME, trip.getStartTime());
		try {
			db.insertOrThrow(TABLE_TRIP, null, values);
		} catch (SQLException e) {
			db.update(TABLE_TRIP, values, COLUMN_TRIP_ID + "=?",
					new String[] { trip.getTripId() });
		}

		closeDatabse();
	}

	public void updateTrip(Trip trip) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_TRIP_FEE, trip.getFee());
		values.put(COLUMN_TRIP_PAYMENT_METHOD, trip.getPaymenMethod());
		values.put(COLUMN_TRIP_STATUS, trip.getStatus());
		values.put(COLUMN_TRIP_DISTANCE, trip.getDistance());
		values.put(COLUMN_TRIP_COMPLETION_TIME, trip.getCompletionTime());
		db.update(TABLE_TRIP, values, COLUMN_TRIP_ID + " = ?",
				new String[] { String.valueOf(trip.getTripId()) });
		closeDatabse();
	}

	public void addListTrip(List<Trip> list) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_TRIP, COLUMN_TRIP_STATUS + " = 'TC'", null);

		ContentValues values = new ContentValues();
		for (int j = 0; j < list.size(); j++) {
			values.put(COLUMN_TRIP_START_LATITUDE, list.get(j)
					.getFromLatitude());
			values.put(COLUMN_TRIP_START_LONGITUDE, list.get(j)
					.getFromLongitude());
			values.put(COLUMN_TRIP_STOP_LATITUDE, list.get(j).getToLatitude());
			values.put(COLUMN_TRIP_STOP_LONGITUDE, list.get(j).getToLatitude());
			values.put(COLUMN_TRIP_STATUS, TripStatus.COMPLETED);
			values.put(COLUMN_TRIP_COMPLETION_TIME, list.get(j)
					.getCompletionTime());
			values.put(COLUMN_TRIP_ID, list.get(j).getTripId());
			values.put(COLUMN_TRIP_RIDER_ID, list.get(j).getRider().getId());
			values.put(COLUMN_TRIP_DISTANCE, list.get(j).getDistance());
			values.put(COLUMN_TRIP_FEE, list.get(j).getFee());
			values.put(COLUMN_TRIP_PAYMENT_METHOD, list.get(j)
					.getPaymenMethod());
			values.put(COLUMN_TRIP_FROM_ADDRESS, list.get(j).getFromAddress());
			values.put(COLUMN_TRIP_TO_ADDRESS, list.get(j).getToAddress());
			values.put(COLUMN_TRIP_START_TIME, list.get(j).getStartTime());

			db.insert(TABLE_TRIP, null, values);
			Rider rider = list.get(j).getRider();
			values = new ContentValues();
			values.put(COLUMN_RIDER_ID, rider.getId());
			values.put(COLUMN_RIDER_FIRST_NAME, rider.getFirstName());
			values.put(COLUMN_RIDER_LAST_NAME, rider.getLastName());
			values.put(COLUMN_RIDER_PHONE, rider.getPhone());
			values.put(COLUMN_RIDER_TYPE, rider.getType());
			try {
				db.insertOrThrow(TABLE_RIDER, null, values);
			} catch (SQLException e) {
				db.update(TABLE_RIDER, values, COLUMN_RIDER_ID + "=?",
						new String[] { rider.getId() });
			}
		}

		closeDatabse();
	}

	public ArrayList<Rider> getListRider() {
		ArrayList<Rider> listRider = new ArrayList<Rider>();
		String selectQuery = "SELECT  * FROM " + TABLE_RIDER + " WHERE "
				+ COLUMN_RIDER_TYPE + " = '" + RiderType.RegularRider + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			listRider = new ArrayList<Rider>();
			do {
				Rider rider = new Rider();
				rider.setId(c.getString(c.getColumnIndex(COLUMN_RIDER_ID)));
				rider.setFirstName(c.getString(c
						.getColumnIndex(COLUMN_RIDER_FIRST_NAME)));
				rider.setLastName(c.getString(c
						.getColumnIndex(COLUMN_RIDER_LAST_NAME)));
				rider.setPhone(c.getString(c.getColumnIndex(COLUMN_RIDER_PHONE)));
				listRider.add(rider);
			} while (c.moveToNext());
		}
		return listRider;
	}

	public void addRider(Rider rider) {
		SQLiteDatabase db = this.getWritableDatabase();
		db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_RIDER_ID, rider.getId());
		values.put(COLUMN_RIDER_FIRST_NAME, rider.getFirstName());
		values.put(COLUMN_RIDER_LAST_NAME, rider.getLastName());
		values.put(COLUMN_RIDER_PHONE, rider.getPhone());
		values.put(COLUMN_RIDER_TYPE, rider.getType());
		values.put(COLUMN_RIDER_IMAGE, rider.getImage());
		try {
			db.insertOrThrow(TABLE_RIDER, null, values);
		} catch (SQLException e) {
			db.update(TABLE_RIDER, values, COLUMN_RIDER_ID + "=?",
					new String[] { rider.getId() });
		}

		if (db != null && db.isOpen())
			db.close();
	}

	public void deleteRider(String riderID) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_RIDER, COLUMN_RIDER_ID + " = ?",
				new String[] { riderID });
		closeDatabse();
	}

	public void deleteAllRider() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RIDER, "1", null);

		closeDatabse();
	}

	public Rider findRiderById(String id) {
		String selectQuery = "SELECT * FROM " + TABLE_RIDER + " WHERE "
				+ COLUMN_RIDER_ID + " = '" + id + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		Rider rider = null;

		if (c.moveToFirst()) {
			rider = new Rider();
			rider.setId(c.getString(c.getColumnIndex(COLUMN_RIDER_ID)));
			rider.setFirstName(c.getString(c
					.getColumnIndex(COLUMN_RIDER_FIRST_NAME)));
			rider.setLastName(c.getString(c
					.getColumnIndex(COLUMN_RIDER_LAST_NAME)));
			rider.setPhone(c.getString(c.getColumnIndex(COLUMN_RIDER_PHONE)));
			rider.setType(c.getString(c.getColumnIndex(COLUMN_RIDER_TYPE)));
			rider.setImage(c.getString(c.getColumnIndex(COLUMN_RIDER_IMAGE)));
		}
		closeDatabse();
		return rider;
	}

	public void updateRider(Rider rider) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_RIDER_FIRST_NAME, rider.getFirstName());
		values.put(COLUMN_RIDER_LAST_NAME, rider.getLastName());
		values.put(COLUMN_RIDER_PHONE, rider.getPhone());
		values.put(COLUMN_RIDER_TYPE, rider.getType());
		db.update(TABLE_RIDER, values, COLUMN_RIDER_ID + " = ?",
				new String[] { String.valueOf(rider.getId()) });
		closeDatabse();

	}

	public void addListRider(List<Rider> list) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RIDER, COLUMN_RIDER_TYPE + " = ?",
				new String[] { RiderType.RegularRider });

		for (int j = 0; j < list.size(); j++) {
			Rider rider = list.get(j);
			db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(COLUMN_RIDER_ID, rider.getId());
			values.put(COLUMN_RIDER_FIRST_NAME, rider.getFirstName());
			values.put(COLUMN_RIDER_LAST_NAME, rider.getLastName());
			values.put(COLUMN_RIDER_PHONE, rider.getPhone());
			values.put(COLUMN_RIDER_TYPE, rider.getType());
			values.put(COLUMN_RIDER_IMAGE, rider.getImage());
			try {
				db.insertOrThrow(TABLE_RIDER, null, values);
			} catch (SQLException e) {
				db.update(TABLE_RIDER, values, COLUMN_RIDER_ID + "=?",
						new String[] { rider.getId() });
			}
		}
		closeDatabse();
	}

	public void addPromotionTrip(PromotionTrip promotionTrip) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_PROMOTION_TRIP_ID, promotionTrip.getId());

		values.put(COLUMN_PROMOTION_TRIP_FROM_ADDRESS,
				promotionTrip.getFromAddress());
		values.put(COLUMN_PROMOTION_TRIP_FROM_CITY, promotionTrip.getFromCity());
		values.put(COLUMN_PROMOTION_TRIP_TO_ADDRESS,
				promotionTrip.getToAddress());
		values.put(COLUMN_PROMOTION_TRIP_TO_CITY, promotionTrip.getToCity());
		values.put(COLUMN_PROMOTION_TRIP_TIME, promotionTrip.getTime());
		values.put(COLUMN_PROMOTION_TRIP_FEE, promotionTrip.getFee());
		values.put(COLUMN_PROMOTION_TRIP_START_LATITUDE,
				promotionTrip.getFromLatitude());
		values.put(COLUMN_PROMOTION_TRIP_START_LONGITUDE,
				promotionTrip.getFromLongitude());
		values.put(COLUMN_PROMOTION_TRIP_STOP_LATITUDE,
				promotionTrip.getToLatitude());
		values.put(COLUMN_PROMOTION_TRIP_STOP_LONGITUDE,
				promotionTrip.getToLatitude());
		values.put(COLUMN_PROMOTION_TRIP_STATUS, TripStatus.NEW_TRIP);
		try {

			db.insert(TABLE_PROMOTION_TRIP, null, values);
		} catch (SQLException e) {
			db.update(TABLE_PROMOTION_TRIP, values, COLUMN_PROMOTION_TRIP_ID
					+ "=?", new String[] { promotionTrip.getId() });
		}
		closeDatabse();
	}

	public void updatePromotionTrip(PromotionTrip promotionTrip) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_PROMOTION_TRIP_FROM_ADDRESS,
				promotionTrip.getFromAddress());
		values.put(COLUMN_PROMOTION_TRIP_FROM_CITY, promotionTrip.getFromCity());
		values.put(COLUMN_PROMOTION_TRIP_TO_ADDRESS,
				promotionTrip.getToAddress());
		values.put(COLUMN_PROMOTION_TRIP_TO_CITY, promotionTrip.getToCity());
		values.put(COLUMN_PROMOTION_TRIP_TIME, promotionTrip.getTime());
		values.put(COLUMN_PROMOTION_TRIP_FEE, promotionTrip.getFee());
		values.put(COLUMN_PROMOTION_TRIP_START_LATITUDE,
				promotionTrip.getFromLatitude());
		values.put(COLUMN_PROMOTION_TRIP_START_LONGITUDE,
				promotionTrip.getFromLongitude());
		values.put(COLUMN_PROMOTION_TRIP_STOP_LATITUDE,
				promotionTrip.getToLatitude());
		values.put(COLUMN_PROMOTION_TRIP_STOP_LONGITUDE,
				promotionTrip.getToLatitude());
		values.put(COLUMN_PROMOTION_TRIP_STATUS, promotionTrip.getStatus());

		db.update(TABLE_PROMOTION_TRIP, values, COLUMN_PROMOTION_TRIP_ID
				+ " =?", new String[] { promotionTrip.getId() });
		closeDatabse();
	}

	public ArrayList<PromotionTrip> getListPromotionTrip() {
		ArrayList<PromotionTrip> listPromotionTrip = new ArrayList<PromotionTrip>();
		String selectQuery = "SELECT  * FROM " + TABLE_PROMOTION_TRIP;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				PromotionTrip promotionTrip = new PromotionTrip();
				promotionTrip.setId(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_ID)));
				promotionTrip.setFromAddress(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_FROM_ADDRESS)));
				promotionTrip.setFromCity(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_FROM_CITY)));
				promotionTrip.setToAddress(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_TO_ADDRESS)));
				promotionTrip.setToCity(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_TO_CITY)));
				promotionTrip.setTime(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_TIME)));
				promotionTrip.setFee(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_FEE))));
				promotionTrip
						.setFromLatitude(Double.parseDouble(c.getString(c
								.getColumnIndex(COLUMN_PROMOTION_TRIP_START_LATITUDE))));
				promotionTrip
						.setFromLongitude(Double.parseDouble(c.getString(c
								.getColumnIndex(COLUMN_PROMOTION_TRIP_START_LONGITUDE))));
				promotionTrip.setToLatitude(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_STOP_LATITUDE))));
				promotionTrip
						.setToLatitude(Double.parseDouble(c.getString(c
								.getColumnIndex(COLUMN_PROMOTION_TRIP_STOP_LONGITUDE))));
				promotionTrip.setStatus(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_STATUS)));
				listPromotionTrip.add(promotionTrip);
			} while (c.moveToNext());
		}
		return listPromotionTrip;
	}

	public void deletePromotionTrip(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PROMOTION_TRIP, COLUMN_PROMOTION_TRIP_ID + "=?",
				new String[] { id });
		db.delete(TABLE_PROMOTION_TRIP_RIDER, COLUMN_PROMOTION_TRIP_ID + "=?",
				new String[] { id });
		closeDatabse();
	}

	public void deletePromotionTrip() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PROMOTION_TRIP, null, null);
		closeDatabse();
	}

	public void deletePromotionTripDetail(String riderId, String promotionTripId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PROMOTION_TRIP_RIDER,
				COLUMN_PROMOTION_TRIP_RIDER_RIDER_ID + " =? AND "
						+ COLUMN_PROMOTION_TRIP_ID + "=?", new String[] {
						riderId, promotionTripId });
		closeDatabse();
	}

	public void addListPromotionTrip(List<PromotionTrip> list) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_TRIP, COLUMN_TRIP_STATUS + " = 'TC'", null);

		ContentValues values = new ContentValues();
		for (int j = 0; j < list.size(); j++) {
			values.put(COLUMN_PROMOTION_TRIP_START_LATITUDE, list.get(j)
					.getFromLatitude());
			values.put(COLUMN_PROMOTION_TRIP_START_LONGITUDE, list.get(j)
					.getFromLongitude());
			values.put(COLUMN_PROMOTION_TRIP_STOP_LATITUDE, list.get(j)
					.getToLatitude());
			values.put(COLUMN_PROMOTION_TRIP_STOP_LONGITUDE, list.get(j)
					.getToLatitude());
			values.put(COLUMN_PROMOTION_TRIP_STATUS, TripStatus.COMPLETED);
			values.put(COLUMN_PROMOTION_TRIP_ID, list.get(j).getId());
			values.put(COLUMN_PROMOTION_TRIP_FEE, list.get(j).getFee());
			values.put(COLUMN_PROMOTION_TRIP_FROM_ADDRESS, list.get(j)
					.getFromAddress());
			values.put(COLUMN_PROMOTION_TRIP_TO_ADDRESS, list.get(j)
					.getToAddress());
			values.put(COLUMN_PROMOTION_TRIP_FROM_CITY, list.get(j)
					.getFromCity());
			values.put(COLUMN_PROMOTION_TRIP_TO_CITY, list.get(j).getToCity());
			values.put(COLUMN_PROMOTION_TRIP_STATUS, list.get(j).getStatus());
			try {
				db.insertOrThrow(TABLE_TRIP, null, values);
			} catch (SQLException ex) {
				db.update(TABLE_PROMOTION_TRIP, values,
						COLUMN_PROMOTION_TRIP_ID + "=?", new String[] { list
								.get(j).getId() });
			}
		}

		closeDatabse();
	}

	public PromotionTrip findPromotionTripById(String id) {
		String selectQuery = "SELECT * FROM " + TABLE_PROMOTION_TRIP
				+ " WHERE " + COLUMN_PROMOTION_TRIP_ID + " = '" + id + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		PromotionTrip promotionTrip = null;

		if (c.moveToFirst()) {
			promotionTrip = new PromotionTrip();
			promotionTrip.setId(c.getString(c
					.getColumnIndex(COLUMN_PROMOTION_TRIP_ID)));
		}
		closeDatabse();
		return promotionTrip;
	}

	public ArrayList<Payment> getListPayment() {
		ArrayList<Payment> listPayment = new ArrayList<Payment>();
		String selectQuery = "SELECT  * FROM " + TABLE_PAYMENT;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			listPayment = new ArrayList<Payment>();
			do {
				Payment payment = new Payment();
				payment.setId(c.getInt(c.getColumnIndex(COLUMN_PAYMENT_ID)));
				payment.setBankName(c.getString(c
						.getColumnIndex(COLUMN_PAYMENT_BANK_NAME)));
				payment.setCardNumber(c.getString(c
						.getColumnIndex(COLUMN_PAYMENT_CARD_NUMBER)));
				payment.setCvv(c.getString(c.getColumnIndex(COLUMN_PAYMENT_CVV)));
				payment.setExpiredMonth(c.getString(c
						.getColumnIndex(COLUMN_PAYMENT_EXPIRED_MONTH)));
				payment.setExpiredYear(c.getString(c
						.getColumnIndex(COLUMN_PAYMENT_EXPIRED_YEAR)));
				payment.setStatus(c.getString(c
						.getColumnIndex(COLUMN_PAYMENT_TYPE)));
				payment.setType(c.getString(c
						.getColumnIndex(COLUMN_PAYMENT_TYPE)));
				listPayment.add(payment);
			} while (c.moveToNext());
		}
		return listPayment;
	}

	public void addPayment(Payment payment) {
		SQLiteDatabase db = this.getWritableDatabase();
		db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_PAYMENT_BANK_NAME, payment.getBankName());
		values.put(COLUMN_PAYMENT_TYPE, payment.getType());
		values.put(COLUMN_PAYMENT_CARD_NUMBER, payment.getCardNumber());
		values.put(COLUMN_PAYMENT_CVV, payment.getCvv());
		values.put(COLUMN_PAYMENT_EXPIRED_MONTH, payment.getExpiredMonth());
		values.put(COLUMN_PAYMENT_EXPIRED_YEAR, payment.getExpiredYear());
		values.put(COLUMN_PAYMENT_ID, payment.getId());
		try {
			db.insertOrThrow(TABLE_PAYMENT, null, values);
		} catch (SQLException e) {
			db.update(TABLE_PAYMENT, values, COLUMN_PAYMENT_ID + "=?",
					new String[] { String.valueOf(payment.getId()) });
		}

		closeDatabse();
	}

	public void updatePayment(Payment payment) {
		SQLiteDatabase db = this.getWritableDatabase();
		db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_PAYMENT_BANK_NAME, payment.getBankName());
		values.put(COLUMN_PAYMENT_TYPE, payment.getType());
		values.put(COLUMN_PAYMENT_CARD_NUMBER, payment.getCardNumber());
		values.put(COLUMN_PAYMENT_CVV, payment.getCvv());
		values.put(COLUMN_PAYMENT_EXPIRED_MONTH, payment.getExpiredMonth());
		values.put(COLUMN_PAYMENT_EXPIRED_YEAR, payment.getExpiredYear());
		db.update(TABLE_PAYMENT, values, COLUMN_PAYMENT_ID + "=?",
				new String[] { String.valueOf(payment.getId()) });
		closeDatabse();
	}

	public void deletePaymentById(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PAYMENT, COLUMN_PAYMENT_ID + "=?",
				new String[] { String.valueOf(id) });
		closeDatabse();
	}

	public void deletePayment() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PAYMENT, "1", null);
		closeDatabse();
	}

	public void addPromotrionTripRiders(PromotionTripRiders tripRiders) {
		SQLiteDatabase db = this.getWritableDatabase();
		db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_PROMOTION_TRIP_RIDER_RIDER_ID, tripRiders.getRider()
				.getId());
		values.put(COLUMN_PROMOTION_TRIP_ID, tripRiders.getPromotionTrip()
				.getId());
		values.put(COLUMN_PROMOTION_TRIP_RIDER_FROM_ADDRESS,
				tripRiders.getFromAddress());
		values.put(COLUMN_PROMOTION_TRIP_RIDER_FROM_CITY,
				tripRiders.getFromCity());
		values.put(COLUMN_PROMOTION_TRIP_RIDER_TO_CITY, tripRiders.getToCity());
		values.put(COLUMN_PROMOTION_TRIP_RIDER_TO_ADDRESS,
				tripRiders.getToAddress());
		values.put(COLUMN_PROMOTION_TRIP_RIDER_TIME, tripRiders.getStartTime());
		values.put(COLUMN_PROMOTION_TRIP_RIDER_NUMER_OF_SEATS,
				tripRiders.getNumberOfSeats());
		values.put(COLUMN_PROMOTION_TRIP_RIDER_STATUS, tripRiders.getStatus());
		try {
			db.insertOrThrow(TABLE_PROMOTION_TRIP_RIDER, null, values);
		} catch (SQLException e) {
			db.update(TABLE_PROMOTION_TRIP_RIDER, values,
					COLUMN_PROMOTION_TRIP_RIDER_RIDER_ID + "=? AND "
							+ COLUMN_PROMOTION_TRIP_ID + "=?", new String[] {
							String.valueOf(tripRiders.getRider().getId()),
							tripRiders.getPromotionTrip().getId() });
		}

		closeDatabse();
	}

	public ArrayList<PromotionTripRiders> getListPromotionTripRiders(
			String promotionTripId) {
		ArrayList<PromotionTripRiders> listTripRiders = new ArrayList<PromotionTripRiders>();
		String selectQuery = "SELECT  * FROM " + TABLE_PROMOTION_TRIP_RIDER
				+ " WHERE " + COLUMN_PROMOTION_TRIP_ID + " = '"
				+ promotionTripId + "' AND "
				+ COLUMN_PROMOTION_TRIP_RIDER_STATUS + " != '"
				+ PromotionTripRiderStatus.REJECT + "'";
		String selectQuery2 = "SELECT  * FROM " + TABLE_RIDER + " WHERE "
				+ COLUMN_RIDER_ID + " = ";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			listTripRiders = new ArrayList<PromotionTripRiders>();
			do {
				PromotionTripRiders tripRiders = new PromotionTripRiders();
				tripRiders.setStatus(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_RIDER_STATUS)));
				tripRiders
						.setFromAddress(c.getString(c
								.getColumnIndex(COLUMN_PROMOTION_TRIP_RIDER_FROM_ADDRESS)));
				tripRiders
						.setFromCity(c.getString(c
								.getColumnIndex(COLUMN_PROMOTION_TRIP_RIDER_FROM_CITY)));
				tripRiders
						.setNumberOfSeats(c.getInt(c
								.getColumnIndex(COLUMN_PROMOTION_TRIP_RIDER_NUMER_OF_SEATS)));
				tripRiders.setStartTime(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_RIDER_TIME)));
				tripRiders
						.setToAddress(c.getString(c
								.getColumnIndex(COLUMN_PROMOTION_TRIP_RIDER_TO_ADDRESS)));
				tripRiders.setToCity(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_RIDER_TO_CITY)));
				Cursor c2 = db
						.rawQuery(
								selectQuery2
										+ "'"
										+ c.getString(c
												.getColumnIndex(COLUMN_PROMOTION_TRIP_RIDER_RIDER_ID))
										+ "'", null);
				if (c2.moveToFirst()) {
					Rider rider = new Rider();
					rider.setId(c2.getString(c2.getColumnIndex(COLUMN_RIDER_ID)));
					rider.setFirstName(c2.getString(c2
							.getColumnIndex(COLUMN_RIDER_FIRST_NAME)));
					rider.setLastName(c2.getString(c2
							.getColumnIndex(COLUMN_RIDER_LAST_NAME)));
					rider.setPhone(c2.getString(c2
							.getColumnIndex(COLUMN_RIDER_PHONE)));
					tripRiders.setRider(rider);
				}
				listTripRiders.add(tripRiders);
			} while (c.moveToNext());
		}
		return listTripRiders;
	}

	public void updatePromotionTripDetails(String riderId,
			String promotionTripId, String status) {
		SQLiteDatabase db = this.getWritableDatabase();
		db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_PROMOTION_TRIP_RIDER_STATUS, status);
		db.update(TABLE_PROMOTION_TRIP_RIDER, values,
				COLUMN_PROMOTION_TRIP_RIDER_RIDER_ID + "=? AND "
						+ COLUMN_PROMOTION_TRIP_ID + "=?", new String[] {
						riderId, promotionTripId });
		closeDatabse();
	}
}
