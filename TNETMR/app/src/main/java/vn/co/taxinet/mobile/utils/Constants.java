package vn.co.taxinet.mobile.utils;

import java.net.URI;

import android.content.Context;
import android.content.Intent;

/**
 * @author Hieu-Gie
 * 
 */
public interface Constants {

	// Google project id
	public static final String SENDER_ID = "943411953393";

	/**
	 * Tag used on log messages.
	 */
	public static final String TAG = "Taxi_Net";

	/*
	 * define Broadcast action name
	 */
	public interface BroadcastAction {
		public static final String DISPLAY_MESSAGE = "vn.co.taxinet.mobile.DISPLAY_MESSAGE";
		public static final String DISPLAY_REQUEST = "vn.co.taxinet.mobile.DISPLAY_REQUEST";
		public static final String PROMOTION_TRIP_REQUEST = "vn.co.taxinet.mobile.PROMOTION_TRIP_REQUEST";
		public static final String TRIP_REQUEST = "vn.co.taxinet.mobile.TRIP_REQUEST";
	}

	public interface PromotionTripRiderStatus {
		public static final String NEWREQUEST = "NR";
		public static final String ACCEPT = "AC";
		public static final String REJECT = "RJ";
		public static final String CANCEL = "CA";
		public static final String REGISTERED = "RG";
	}

	/*
	 * define url
	 */
	public String server = "http://192.168.1.78:8080/TN/restServices/";

	public interface URL {
		public static final String GET_TERM = server + "TermController/GetTerm";
		public static final String LOGIN_AUTHEN = server
				+ "riderController/Login";
		public static final String UPDATE_RIDER = server
				+ "riderController/UpdateRider";
		public static final String UPDATE_RIDER_ADDRESS = server
				+ "riderController/UpdateRiderAddress";
		public static final String LOGOUT_RIDER = server
				+ "riderController/Logout";
		public static final String UPDATE_REG_ID = server
				+ "CommonController/UpdateRegId";
		public static final String REGISTER_RIDER = server
				+ "CommonController/register";
		public static final String UPDATE_CURRENT_STATUS = server
				+ "DriverController/UpdateCurrentStatus";
		public static final String UPDATE_TRIP = server
				+ "TripController/UpdateTrip";
		public static final String CREATE_TRIP = server
				+ "TripController/CreateTrip";
		public static final String GET_DRIVER = server
				+ "DriverController/getNearDriver";
		public static final String CHANGE_PASSWORD = server
				+ "riderController/ChangePassword";
		public static final String GET_LIST_COMPELTE_TRIP_RIDER = server
				+ "TripController/GetListCompleteTripRider";
		public static final String ADD_FAVORITE_DRIVER = server
				+ "FavoriteController/addFavoriteDriver";
		public static final String GET_FAVORITE_DRIVER = server
				+ "FavoriteController/getListFavoriteDriver";
		public static final String DELETE_FAVORITE_DRIVER = server
				+ "FavoriteController/deleteFavoriteDriver";
		public static final String FIND_PROMOTION_TRIP = server
				+ "PromotionTripController/FindPromotionTip";
		public static final String UPDATE_PROMOTION_TRIP_DETAILS = server
				+ "PromotionTripController/UpdatePromotionTripDetails";
		public static final String REGISTER_PROMOTION_TRIP = server
				+ "PromotionTripController/RegisterPromotionTip";

		public static final String RESET_PASSWORD = server
				+ "riderController/ResetPassword";
		public static final String SEND_RESPONSE_EMAIL = server
				+ "riderController/SendFeedBack";
		public static final String LOGOUT = server + "riderController/Logout";
		public static final String GET_LIST_REGISTERED_PROMOTION_TRIP = server
				+ "PromotionTripController/GetListPromotionTripRider";
	}

	/*
	 * define String const
	 */
	public static final String SUCCESS = "SUCCESS";
	public static final String ACCEPT = "OK";
	public static final String DENI = "DENI";
	public static final String EMPTY_ERROR = "EMPTY_ERROR";
	public static final String EMAIL_ERROR = "EMAIL_ERROR";
	public static final String PHONE_NUMBER_ERROR = "PHONE_NUMBER_ERROR";
	public static final String FIRST_NAME_ERROR = "FIRST_NAME_ERROR";
	public static final String LAST_NAME_ERROR = "LAST_NAME_ERROR";
	public static final String PASSWORD_ERROR = "PASSWORD_ERROR";
	public static final String ACCOUNT_ERROR = "ACCOUNT_ERROR";
	public static final String EXTRA_MESSAGE = "MESSGE";
	public static final String DRIVER_IMAGE = "DRIVER_IMAGE";
	public static final String DRIVER_NAME = "DRIVER_NAME";
	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	public static final String PRICE = "PRICE";
	public static final String IMAGE = "image";
	public static final String NAME = "name";
	public static final String PHONE = "phone";
	public static final String ID = "id";
	public static final String RESPONSE_REQUEST = "RESPONSE_REQUEST";
	public static final String UPDATE_CURRENT_STATUS = "UPDATE_CURRENT_STATUS";
	public static final String PROMOTION_TRIP = "PROMOTION_TRIP";
	public static final String TRIP = "TRIP";
	public static final String TNET_EMAIL = "hieudtse02895@gmail.com";

	public static final String DATA_NOT_FOUND = "Data not found";

	public static final String FAIL = "Fail";

	public interface DriverStatus {
		public static final String NEW = "NE";
		public static final String APPROVED = "AP";
		public static final String AVAIABLE = "AC";
		public static final String BUSY = "BU";
		public static final String OUT_OF_SERVICE = "OS";
		public static final String NOT_AVAIABLE = "NA";
	}

	public interface TripStatus {
		public static final String NEW_TRIP = "NT";
		public static final String REJECTED = "RJ";
		public static final String PICKED = "PD";
		public static final String PICKING = "PI";
		public static final String CANCELLED = "CA";
		public static final String COMPLETED = "TC";
	}

	// These are used for Reference Data
	public interface UserGroup {
		public static final String RIDER = "RD";
		public static final String DRIVER = "DR";
		public static final String AGENT = "AG";
		public static final String AREA_STAFF = "AS";
		public static final String COUNTRY_STAFF = "CS";
		public static final String AREA_MANAGER = "AM";
		public static final String COUNTRY_MANAGER = "CM";
		public static final String COUNTRY_ADMINISTRATOR = "CA";
		public static final String GLOBAL_MANAGER = "GM";
		public static final String GLOBAL_ADMINISTRATOR = "GA";
	}

	public interface RiderType {
		public static final String RegularRider = "RR";
		public static final String StrangerRider = "SR";
	}

	public interface DriverType {
		public static final String RegularDriver = "RD";
		public static final String StrangeDriver = "SD";
	}

	public interface PromotionTripStatus {
		public static final String NEW_TRIP = "NT";
		public static final String CANCEL = "CA";
		public static final String REJECT = "RJ";
		public static final String ACCEPT = "AC";
	}

	public interface Message {
		public static final String SUCCESS = "SUCCESS";
		public static final String CANCEL = "CANCEL";
		public static final String FAIL = "FAIL";
		public static final String ERROR = "ERROR";
		public static final String REQUEST_NOT_FOUND = "REQUEST_NOT_FOUND";
		public static final String NUMBER_FORMAT_EXCEPTION = "NUMBER_FORMAT_EXCEPTION";
		public static final String EMTPY_STATUS = "EMTPY_STATUS";
		public static final String NULL_PARAMS = "NULL_PARAMS";
		public static final String DATA_NOT_FOUND = "DATA_NOT_FOUND";
		public static final String PASSWORD_LENGTH = "PASSWORD_LENGTH";
		public static final String PASSWORD_NOT_SAME = "PASSWORD_NOT_SAME";
		public static final String PASSWORD_ERROR = "PASSWORD_ERROR";
		public static final String EMAIL_ERROR = "EMAIL ERROR";
		public static final String PHONE_ERROR = "PHONE NUMBER ERROR";
		public static final String EXIST_ACCOUNT = "EXIST_ACCOUNT";
	}

	public interface NotificationType {
		public static final String TRIP = "TRIP";
		public static final String PROMOTION_TRIP = "PROMOTION_TRIP";
	}

	public interface LOGIN_ERROR {
		public static final String WRONG_EMAIL_OR_PASSWORD = "WRONG_EMAIL_OR_PASSWORD";
		public static final String ACCOUNT_NOT_ACTIVE = "ACCOUNT_NOT_ACTIVE";
	}

	public interface LanguageCode {
		public static final String VI = "vi";
		public static final String EN = "en";
	}
}
