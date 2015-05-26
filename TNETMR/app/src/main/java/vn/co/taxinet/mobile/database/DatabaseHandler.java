package vn.co.taxinet.mobile.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.model.Rider;
import vn.co.taxinet.mobile.model.Trip;
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
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "TaxiNet";

	// Table Names
	private static final String TABLE_RIDER = "RIDER";
	private static final String TABLE_TERM = "term";
	private static final String TABLE_TRIP = "TABLE_TRIP";
	private static final String TABLE_DRIVER = "TABLE_DRIVER";
	private static final String TABLE_PROMOTION_TRIP = "TABLE_PROMOTION_TRIP";

	// DRIVER Table - column names
	private static final String COLUMN_DRIVER_ID = "COLUMN_DRIVER_ID";
	private static final String COLUMN_DRIVER_IMAGES = "COLUMN_DRIVER_IMAGES";
	private static final String COLUMN_DRIVER_FIRST_NAME = "COLUMN_DRIVER_FIRST_NAME";
	private static final String COLUMN_DRIVER_LAST_NAME = "COLUMN_DRIVER_LAST_NAME";
	private static final String COLUMN_DRIVER_EMAIL = "COLUMN_DRIVER_EMAIL";
	private static final String COLUMN_DRIVER_PHONE_NUMBER = "COLUMN_DRIVER_PHONE_NUMBER";
	private static final String COLUMN_DRIVER_TYPE = "COLUMN_DRIVER_TYPE";

	// RIDER Table - column names
	private static final String COLUMN_RIDER_ID = "COLUMN_RIDER_ID";
	private static final String COLUMN_RIDER_IMAGES = "COLUMN_RIDER_IMAGES";
	private static final String COLUMN_RIDER_FIRST_NAME = "COLUMN_RIDER_FIRST_NAME";
	private static final String COLUMN_RIDER_LAST_NAME = "COLUMN_RIDER_LAST_NAME";
	private static final String COLUMN_RIDER_EMAIL = "COLUMN_RIDER_EMAIL";
	private static final String COLUMN_RIDER_PASSWORD = "COLUMN_RIDER_PASSWORD";
	private static final String COLUMN_RIDER_PHONE_NUMBER = "COLUMN_RIDER_PHONE_NUMBER";
	private static final String COLUMN_RIDER_HOME_ADDRESS = "COLUMN_RIDER_HOME_ADDRESS";
	private static final String COLUMN_RIDER_HOME_ADDRESS_LAT = "COLUMN_RIDER_HOME_ADDRESS_LAT";
	private static final String COLUMN_RIDER_HOME_ADDRESS_LNG = "COLUMN_RIDER_HOME_ADDRESS_LNG";
	private static final String COLUMN_RIDER_OFFICE_ADDRESS = "COLUMN_RIDER_OFFICE_ADDRESS";
	private static final String COLUMN_RIDER_OFFICE_ADDRESS_LAT = "COLUMN_RIDER_OFFICE_ADDRESS_LAT";
	private static final String COLUMN_RIDER_OFFICE_ADDRESS_LNG = "COLUMN_RIDER_OFFICE_ADDRESS_LNG";

	// TERM Table - column names
	private static final String COLUMN_TERM_ID = "COLUMN_TERM_ID";
	private static final String COLUMN_TERM_CONTENT = "COLUMN_TERM_CONTENT";

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
	private static final String COLUMN_TRIP_DISTANCE = "COLUMN_TRIP_REAL_DISTANCE";
	private static final String COLUMN_TRIP_FROM_ADDRESS = "COLUMN_TRIP_FROM_ADDRESS";
	private static final String COLUMN_TRIP_TO_ADDRESS = "COLUMN_TRIP_TO_ADDRESS";
	private static final String COLUMN_TRIP_DRIVER_ID = "COLUMN_TRIP_RIDER_ID";

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
	private static final String COLUMN_PROMOTION_TRIP_CAPACITY = "COLUMN_PROMOTION_TRIP_CAPACITY";
	private static final String COLUMN_PROMOTION_TRIP_DRIVER_ID = "COLUMN_PROMOTION_TRIP_RIDER_ID";

	private static final String CREATE_TABLE_DRIVER = "CREATE TABLE "
			+ TABLE_DRIVER + "(" + COLUMN_DRIVER_ID + " TEXT,"
			+ COLUMN_DRIVER_IMAGES + " TEXT," + COLUMN_DRIVER_FIRST_NAME
			+ " TEXT," + COLUMN_DRIVER_LAST_NAME + " TEXT,"
			+ COLUMN_DRIVER_EMAIL + " TEXT," + COLUMN_DRIVER_TYPE + " TEXT,"
			+ COLUMN_DRIVER_PHONE_NUMBER + " TEXT" + ")";

	// RIDER table create statement
	private static final String CREATE_TABLE_RIDER = "CREATE TABLE "
			+ TABLE_RIDER + "(" + COLUMN_RIDER_ID + " TEXT,"
			+ COLUMN_RIDER_IMAGES + " TEXT," + COLUMN_RIDER_FIRST_NAME
			+ " TEXT," + COLUMN_RIDER_LAST_NAME + " TEXT," + COLUMN_RIDER_EMAIL
			+ " TEXT," + COLUMN_RIDER_PASSWORD + " TEXT,"
			+ COLUMN_RIDER_PHONE_NUMBER + " TEXT," + COLUMN_RIDER_HOME_ADDRESS
			+ " TEXT," + COLUMN_RIDER_HOME_ADDRESS_LAT + " TEXT,"
			+ COLUMN_RIDER_HOME_ADDRESS_LNG + " TEXT,"
			+ COLUMN_RIDER_OFFICE_ADDRESS_LAT + " TEXT,"
			+ COLUMN_RIDER_OFFICE_ADDRESS_LNG + " TEXT,"
			+ COLUMN_RIDER_OFFICE_ADDRESS + " TEXT" + ")";

	// TERM table create statement
	private static final String CREATE_TABLE_TERM = "CREATE TABLE "
			+ TABLE_TERM + "(" + COLUMN_TERM_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TERM_CONTENT
			+ " TEXT" + ")";

	// TRIP table create statement
	private static final String CREATE_TABLE_TRIP = "CREATE TABLE "
			+ TABLE_TRIP + "(" + COLUMN_TRIP_ID + " TEXT," + COLUMN_TRIP_FEE
			+ " TEXT," + COLUMN_TRIP_PAYMENT_METHOD + " TEXT,"
			+ COLUMN_TRIP_START_LATITUDE + " TEXT,"
			+ COLUMN_TRIP_START_LONGITUDE + " TEXT,"
			+ COLUMN_TRIP_STOP_LATITUDE + " TEXT," + COLUMN_TRIP_STOP_LONGITUDE
			+ " TEXT," + COLUMN_TRIP_COMPLETION_TIME + " TEXT,"
			+ COLUMN_TRIP_DRIVER_ID + " TEXT," + COLUMN_TRIP_FROM_ADDRESS
			+ " TEXT," + COLUMN_TRIP_TO_ADDRESS + " TEXT,"
			+ COLUMN_TRIP_DISTANCE + " TEXT," + COLUMN_TRIP_STATUS + " TEXT"
			+ ")";
	// Promotion Trip create statement
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
			+ COLUMN_PROMOTION_TRIP_DRIVER_ID + " TEXT,"
			+ COLUMN_PROMOTION_TRIP_CAPACITY + " TEXT,"
			+ COLUMN_PROMOTION_TRIP_STATUS + " TEXT" + ")";

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
		db.execSQL(CREATE_TABLE_RIDER);
		db.execSQL(CREATE_TABLE_TERM);
		db.execSQL(CREATE_TABLE_TRIP);
		db.execSQL(CREATE_TABLE_DRIVER);
		db.execSQL(CREATE_TABLE_PROMOTION_TRIP);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERM);
		// create new tables
		onCreate(db);
	}

	// -------------------common methods---------------------------------//
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public void deleteAllData() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_DRIVER, "1", null);
		db.delete(TABLE_TRIP, "1", null);
		db.delete(TABLE_RIDER, "1", null);
		db.delete(TABLE_PROMOTION_TRIP, "1", null);

		closeDatabse();

	}

	// ------------------------ "RIDER" table methods
	// -----------------------------------//
	/*
	 * Add a term
	 */

	public long createTerm(String content) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_TERM_CONTENT, content);

		// Inserting Row
		long _id = db.insert(TABLE_TERM, null, values);
		if (db != null && db.isOpen())
			db.close();
		return _id;
	}

	/*
	 * Deleting a term
	 */
	public void deleteTerm() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_TERM);

		if (db != null && db.isOpen())
			db.close();

	}

	public void addDriver(Driver driver) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_DRIVER_ID, driver.getId());
		values.put(COLUMN_DRIVER_FIRST_NAME, driver.getFirstName());
		values.put(COLUMN_DRIVER_LAST_NAME, driver.getLastName());
		values.put(COLUMN_DRIVER_IMAGES, driver.getImage());
		values.put(COLUMN_DRIVER_PHONE_NUMBER, driver.getPhoneNumber());
		values.put(COLUMN_DRIVER_TYPE, driver.getType());
		db.insert(TABLE_DRIVER, null, values);
		closeDatabse();
	}

	public Driver findDriver() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_DRIVER;
		Cursor c = db.rawQuery(selectQuery, null);
		Driver driver = null;
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			driver = new Driver();
			driver.setId(c.getString(c.getColumnIndex(COLUMN_DRIVER_ID)));
			driver.setFirstName((c.getString(c
					.getColumnIndex(COLUMN_DRIVER_FIRST_NAME))));
			driver.setLastName(c.getString(c
					.getColumnIndex(COLUMN_DRIVER_LAST_NAME)));
			driver.setImage(c.getString(c.getColumnIndex(COLUMN_DRIVER_IMAGES)));
			driver.setPhoneNumber(c.getString(c
					.getColumnIndex(COLUMN_DRIVER_PHONE_NUMBER)));
			driver.setType(c.getString(c.getColumnIndex(COLUMN_DRIVER_TYPE)));
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
		values.put(COLUMN_DRIVER_PHONE_NUMBER, driver.getPhoneNumber());
		values.put(COLUMN_DRIVER_TYPE, driver.getType());
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
				driver.setPhoneNumber(c.getString(c
						.getColumnIndex(COLUMN_DRIVER_PHONE_NUMBER)));
				driver.setType(c.getString(c.getColumnIndex(COLUMN_DRIVER_TYPE)));
				listDrivers.add(driver);
			} while (c.moveToNext());
		}

		closeDatabse();

		return listDrivers;
	}

	public long createRider(Rider rider) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_RIDER_ID, rider.getId());
		values.put(COLUMN_RIDER_FIRST_NAME, rider.getFirstname());
		values.put(COLUMN_RIDER_LAST_NAME, rider.getLastname());
		values.put(COLUMN_RIDER_IMAGES, rider.getImage());
		values.put(COLUMN_RIDER_EMAIL, rider.getEmail());
		values.put(COLUMN_RIDER_PHONE_NUMBER, rider.getPhone());
		if (!rider.getHome_detail().equalsIgnoreCase("null")) {
			values.put(COLUMN_RIDER_HOME_ADDRESS, rider.getHome_detail());
			values.put(COLUMN_RIDER_HOME_ADDRESS_LAT, rider.getHome_lat());
			values.put(COLUMN_RIDER_HOME_ADDRESS_LNG, rider.getHome_lng());
		}
		if (!rider.getWork_detail().equalsIgnoreCase("null")) {
			values.put(COLUMN_RIDER_OFFICE_ADDRESS, rider.getWork_detail());
			values.put(COLUMN_RIDER_OFFICE_ADDRESS_LAT, rider.getWork_lat());
			values.put(COLUMN_RIDER_OFFICE_ADDRESS_LNG, rider.getWork_lng());
		}

		// Inserting Row
		long _id = db.insert(TABLE_RIDER, null, values);
		if (db != null && db.isOpen())
			db.close();
		return _id;
	}

	/*
	 * find a RIDER
	 */
	public Rider findRider() {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_RIDER;

		Cursor c = db.rawQuery(selectQuery, null);
		Rider rider = null;
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			rider = new Rider();
			rider.setId(c.getString(c.getColumnIndex(COLUMN_RIDER_ID)));
			rider.setFirstname((c.getString(c
					.getColumnIndex(COLUMN_RIDER_FIRST_NAME))));
			rider.setLastname((c.getString(c
					.getColumnIndex(COLUMN_RIDER_LAST_NAME))));
			rider.setImage(c.getString(c.getColumnIndex(COLUMN_RIDER_IMAGES)));
			rider.setEmail(c.getString(c.getColumnIndex(COLUMN_RIDER_EMAIL)));
			rider.setPhone(c.getString(c
					.getColumnIndex(COLUMN_RIDER_PHONE_NUMBER)));
			if (c.getString(c.getColumnIndex(COLUMN_RIDER_HOME_ADDRESS)) != null) {
				rider.setHome_detail(c.getString(c
						.getColumnIndex(COLUMN_RIDER_HOME_ADDRESS)));
				rider.setHome_lat(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_RIDER_HOME_ADDRESS_LAT))));
				rider.setHome_lng(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_RIDER_HOME_ADDRESS_LNG))));
			}
			if (c.getString(c.getColumnIndex(COLUMN_RIDER_OFFICE_ADDRESS)) != null) {
				rider.setWork_detail(c.getString(c
						.getColumnIndex(COLUMN_RIDER_OFFICE_ADDRESS)));
				rider.setWork_lat(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_RIDER_OFFICE_ADDRESS_LAT))));
				rider.setWork_lng(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_RIDER_OFFICE_ADDRESS_LNG))));
			}
		}

		if (db != null && db.isOpen())
			db.close();

		return rider;
	}

	/*
	 * Updating a RIDER
	 */
	public int updateRider(Rider rider) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_RIDER_ID, rider.getId());
		values.put(COLUMN_RIDER_FIRST_NAME, rider.getFirstname());
		values.put(COLUMN_RIDER_LAST_NAME, rider.getLastname());
		values.put(COLUMN_RIDER_IMAGES, rider.getImage());
		values.put(COLUMN_RIDER_EMAIL, rider.getEmail());
		values.put(COLUMN_RIDER_PHONE_NUMBER, rider.getPhone());
		if (rider.getHome_detail() != null) {
			values.put(COLUMN_RIDER_HOME_ADDRESS, rider.getHome_detail());
			values.put(COLUMN_RIDER_HOME_ADDRESS_LAT, rider.getHome_lat());
			values.put(COLUMN_RIDER_HOME_ADDRESS_LNG, rider.getHome_lng());
		}
		if (rider.getWork_detail() != null) {
			values.put(COLUMN_RIDER_OFFICE_ADDRESS, rider.getWork_detail());
			values.put(COLUMN_RIDER_OFFICE_ADDRESS_LAT, rider.getWork_lat());
			values.put(COLUMN_RIDER_OFFICE_ADDRESS_LNG, rider.getWork_lng());
		}
		db.execSQL("delete from " + TABLE_RIDER);

		// updating row
		// int temp = db.update(TABLE_RIDER, values, COLUMN_RIDER_ID + " = ?",
		// new String[] { String.valueOf(rider.getId()) });
		int temp = (int) db.insert(TABLE_RIDER, null, values);
		if (db != null && db.isOpen())
			db.close();
		return temp;
	}

	/*
	 * Deleting a RIDER
	 */
	public void deleteRiderById() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_RIDER);

		if (db != null && db.isOpen())
			db.close();

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
			rider.setFirstname(c.getString(c
					.getColumnIndex(COLUMN_RIDER_FIRST_NAME)));
			rider.setLastname(c.getString(c
					.getColumnIndex(COLUMN_RIDER_LAST_NAME)));
			rider.setPhone(c.getString(c
					.getColumnIndex(COLUMN_RIDER_PHONE_NUMBER)));
		}
		closeDatabse();
		return rider;
	}

	public Driver findDriverById(String id) {
		String selectQuery = "SELECT * FROM " + TABLE_DRIVER + " WHERE "
				+ COLUMN_DRIVER_ID + " = '" + id + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		Driver driver = null;

		if (c.moveToFirst()) {
			driver = new Driver();
			driver.setId(c.getString(c.getColumnIndex(COLUMN_DRIVER_ID)));
			driver.setFirstName(c.getString(c
					.getColumnIndex(COLUMN_DRIVER_FIRST_NAME)));
			driver.setLastName(c.getString(c
					.getColumnIndex(COLUMN_DRIVER_LAST_NAME)));
			driver.setPhoneNumber(c.getString(c
					.getColumnIndex(COLUMN_DRIVER_PHONE_NUMBER)));
			driver.setType(c.getString(c.getColumnIndex(COLUMN_DRIVER_TYPE)));
		}
		closeDatabse();
		return driver;
	}

	public void addRider(Rider rider) {
		SQLiteDatabase db = this.getWritableDatabase();
		db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_RIDER_ID, rider.getId());
		values.put(COLUMN_RIDER_FIRST_NAME, rider.getFirstname());
		values.put(COLUMN_RIDER_LAST_NAME, rider.getLastname());
		values.put(COLUMN_RIDER_PHONE_NUMBER, rider.getPhone());
		db.insert(TABLE_RIDER, null, values);

		if (db != null && db.isOpen())
			db.close();
	}

	public ArrayList<Rider> getListRider() {
		ArrayList<Rider> listRiders = new ArrayList<Rider>();
		String selectQuery = "SELECT  * FROM " + TABLE_RIDER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Rider rider = new Rider();
				rider.setId(c.getString(c.getColumnIndex(COLUMN_RIDER_ID)));
				rider.setFirstname((c.getString(c
						.getColumnIndex(COLUMN_RIDER_FIRST_NAME))));
				rider.setLastname((c.getString(c
						.getColumnIndex(COLUMN_RIDER_LAST_NAME))));
				rider.setImage(c.getString(c
						.getColumnIndex(COLUMN_RIDER_IMAGES)));
				rider.setEmail(c.getString(c.getColumnIndex(COLUMN_RIDER_EMAIL)));
				rider.setPhone(c.getString(c
						.getColumnIndex(COLUMN_RIDER_PHONE_NUMBER)));
				listRiders.add(rider);
			} while (c.moveToNext());
		}

		if (db != null && db.isOpen())
			db.close();

		return listRiders;
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
			trip.setStartLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_START_LATITUDE))));
			trip.setStartLongitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_START_LONGITUDE))));
			trip.setStatus(c.getString(c.getColumnIndex(COLUMN_TRIP_STATUS)));
			trip.setStopLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_STOP_LATITUDE))));
			trip.setStopLongitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_STOP_LONGITUDE))));
			trip.setTripId(c.getString(c.getColumnIndex(COLUMN_TRIP_ID)));
			Driver driver = new Driver();
			driver.setId(c.getString(c.getColumnIndex(COLUMN_TRIP_DRIVER_ID)));
			trip.setDriver(driver);
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
				trip.setStartLatitude(Double.parseDouble(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_START_LATITUDE))));
				trip.setStartLongitude(Double.parseDouble(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_START_LONGITUDE))));
				trip.setStopLatitude(Double.parseDouble(c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_STOP_LATITUDE))));
				trip.setStopLongitude(Double.parseDouble(c1.getString(c1
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
				String driverId = c1.getString(c1
						.getColumnIndex(COLUMN_TRIP_DRIVER_ID));
				Driver driver = findDriverById(driverId);
				trip.setDriver(driver);
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
				trip.setStartLatitude(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_TRIP_START_LATITUDE))));
				trip.setStartLongitude(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_TRIP_START_LONGITUDE))));
				trip.setStopLatitude(Double.parseDouble(c.getString(c
						.getColumnIndex(COLUMN_TRIP_STOP_LATITUDE))));
				trip.setStopLongitude(Double.parseDouble(c.getString(c
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
				String driverId = c.getString(c
						.getColumnIndex(COLUMN_TRIP_DRIVER_ID));
				Driver driver = findDriverById(driverId);
				trip.setDriver(driver);
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
			trip.setStartLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_START_LATITUDE))));
			trip.setStartLongitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_START_LONGITUDE))));
			trip.setStatus(c.getString(c.getColumnIndex(COLUMN_TRIP_STATUS)));
			trip.setStopLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_STOP_LATITUDE))));
			trip.setStopLongitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_TRIP_STOP_LONGITUDE))));
			Driver driver = new Driver();
			driver.setId(c.getString(c.getColumnIndex(COLUMN_TRIP_DRIVER_ID)));
			trip.setDriver(driver);
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
		values.put(COLUMN_TRIP_START_LATITUDE, trip.getStartLatitude());
		values.put(COLUMN_TRIP_START_LONGITUDE, trip.getStartLongitude());
		values.put(COLUMN_TRIP_STATUS, trip.getStatus());
		values.put(COLUMN_TRIP_STOP_LATITUDE, trip.getStopLatitude());
		values.put(COLUMN_TRIP_STOP_LONGITUDE, trip.getStopLongitude());
		values.put(COLUMN_TRIP_COMPLETION_TIME, trip.getCompletionTime());
		values.put(COLUMN_TRIP_FROM_ADDRESS, trip.getFromAddress());
		values.put(COLUMN_TRIP_TO_ADDRESS, trip.getToAddress());
		values.put(COLUMN_TRIP_DRIVER_ID, trip.getDriver().getId());
		long i = db.insert(TABLE_TRIP, null, values);
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

	public boolean checkExistPromotionTrip(String promotiontripId) {
		String selectQuery = "SELECT * FROM " + TABLE_PROMOTION_TRIP
				+ " WHERE " + COLUMN_PROMOTION_TRIP_ID + " = '"
				+ promotiontripId + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		PromotionTrip promotionTrip = null;

		if (c.moveToFirst()) {
			closeDatabse();
			return true;
		}
		return false;
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
		values.put(COLUMN_PROMOTION_TRIP_DRIVER_ID, promotionTrip.getDriver()
				.getId());
		values.put(COLUMN_PROMOTION_TRIP_STATUS, promotionTrip.getStatus());
		values.put(COLUMN_PROMOTION_TRIP_CAPACITY, promotionTrip.getCapacity());
		try {

			long i = db.insert(TABLE_PROMOTION_TRIP, null, values);
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
		values.put(COLUMN_PROMOTION_TRIP_CAPACITY, promotionTrip.getCapacity());
		values.put(COLUMN_PROMOTION_TRIP_DRIVER_ID, promotionTrip.getDriver()
				.getId());
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
				promotionTrip.setCapacity(Integer.parseInt(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_CAPACITY))));
				String driverId = c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_DRIVER_ID));
				Driver driver = findDriverById(driverId);
				promotionTrip.setDriver(driver);
				promotionTrip.setStatus(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_STATUS)));
				listPromotionTrip.add(promotionTrip);
			} while (c.moveToNext());
		}
		return listPromotionTrip;
	}

	public ArrayList<PromotionTrip> getListRegisteredPromotionTrip() {
		ArrayList<PromotionTrip> listPromotionTrip = new ArrayList<PromotionTrip>();
		// TODO
		String selectQuery = "SELECT  * FROM " + TABLE_PROMOTION_TRIP
				+ " WHERE " + COLUMN_PROMOTION_TRIP_STATUS + " != 'NT' ";
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
				promotionTrip.setCapacity(Integer.parseInt(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_CAPACITY))));
				String driverId = c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_DRIVER_ID));
				Driver driver = findDriverById(driverId);
				promotionTrip.setDriver(driver);
				promotionTrip.setStatus(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_STATUS)));
				listPromotionTrip.add(promotionTrip);
			} while (c.moveToNext());
		}
		return listPromotionTrip;
	}
	
	public ArrayList<PromotionTrip> getListUnRegisteredPromotionTrip() {
		ArrayList<PromotionTrip> listPromotionTrip = new ArrayList<PromotionTrip>();
		// TODO
		String selectQuery = "SELECT  * FROM " + TABLE_PROMOTION_TRIP
				+ " WHERE " + COLUMN_PROMOTION_TRIP_STATUS + " == 'NT' ";
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
				promotionTrip.setCapacity(Integer.parseInt(c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_CAPACITY))));
				String driverId = c.getString(c
						.getColumnIndex(COLUMN_PROMOTION_TRIP_DRIVER_ID));
				Driver driver = findDriverById(driverId);
				promotionTrip.setDriver(driver);
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
		closeDatabse();
	}

	public void deleteUnRegisterPromotionTrip() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PROMOTION_TRIP, COLUMN_PROMOTION_TRIP_STATUS + "=?",
				new String[] { "NT" });
		closeDatabse();
	}

	public void deletePromotionTrip() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PROMOTION_TRIP, null, null);
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
			values.put(COLUMN_PROMOTION_TRIP_CAPACITY, list.get(j)
					.getCapacity());
			values.put(COLUMN_PROMOTION_TRIP_STATUS, list.get(j).getStatus());
			values.put(COLUMN_PROMOTION_TRIP_DRIVER_ID, list.get(j).getDriver()
					.getId());
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
			promotionTrip.setFromLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_PROMOTION_TRIP_START_LATITUDE))));
			promotionTrip.setFromLongitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_PROMOTION_TRIP_START_LONGITUDE))));
			promotionTrip.setToLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_PROMOTION_TRIP_STOP_LATITUDE))));
			promotionTrip.setToLatitude(Double.parseDouble(c.getString(c
					.getColumnIndex(COLUMN_PROMOTION_TRIP_STOP_LONGITUDE))));
			promotionTrip.setCapacity(Integer.parseInt(c.getString(c
					.getColumnIndex(COLUMN_PROMOTION_TRIP_CAPACITY))));
			String driverId = c.getString(c
					.getColumnIndex(COLUMN_PROMOTION_TRIP_DRIVER_ID));
			Driver driver = findDriverById(driverId);
			promotionTrip.setDriver(driver);
			promotionTrip.setStatus(c.getString(c
					.getColumnIndex(COLUMN_PROMOTION_TRIP_STATUS)));
		}
		closeDatabse();
		return promotionTrip;
	}

	public void closeDatabse() {
		SQLiteDatabase db = getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

}
