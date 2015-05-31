package mobile.taxinet.co.vn.mdnew.utils;


/**
 * @author Hieu-Gie
 * 
 */
public interface Constants {

	// Google project id
	public static final String SENDER_ID = "787906349249";

	/**
	 * Tag used on log messages.
	 */
	public static final String TAG = "Taxi_Net";

	/*
	 * define Broadcast action name
	 */
	public interface BroadcastAction {
		public static final String DISPLAY_MESSAGE = "vn.co.taxinet.mobile.DISPLAY_MESSAGE";
		public static final String TRIP_REQUEST = "vn.co.taxinet.mobile.TRIP_REQUEST";
		public static final String PROMOTION_TRIP_REQUEST = "vn.co.taxinet.mobile.PROMOTION_TRIP_REQUEST";
	}

	/*
	 * define url
	 */
	public interface URL {
		public String server = "http://192.168.100.5:8080/TN/restServices/";

		public static final String GET_TERM = server + "TermController/GetTerm";
		public static final String LOGIN_AUTHEN = server
				+ "DriverController/Login";
		public static final String UPDATE_DRIVER = server
				+ "DriverController/UpdateDriver";
		public static final String UPDATE_CURRENT_STATUS = server
				+ "DriverController/UpdateCurrentStatus";
		public static final String UPDATE_REG_ID = server
				+ "CommonController/UpdateRegId";
		public static final String REGISTER_DRIVER = server
				+ "CommonController/register";
		public static final String UPDATE_TRIP = server
				+ "TripController/UpdateTrip";
		public static final String COMPLETE_TRIP = server
				+ "TripController/CompleteTrip";
		public static final String GET_LIST_COMPELTE_TRIP = server
				+ "TripController/GetListCompleteTrip";
		public static final String CHANGE_PASSWORD = server
				+ "DriverController/ChangePassword";
		public static final String LOGOUT = server + "DriverController/Logout";
		public static final String GET_COMPANY = server
				+ "CompanyController/findCompanyByDriverId";
		public static final String GET_VIP_RIDER = server
				+ "FavoriteController/getListFavoriteRider";
		public static final String DELETE_FAVORITE_RIDER = server
				+ "FavoriteController/deleteFavoriteRider";
		public static final String ADD_FAVORITE_RIDER = server
				+ "FavoriteController/addFavoriteRider";
		public static final String REGISTER_PROMOTION_TRIP = server
				+ "PromotionTripController/AddPromotionTrip";
		public static final String DELETE_PROMOTION_TRIP = server
				+ "PromotionTripController/DeletePromotionTrip";
		public static final String GET_LIST_PROMOTION_TRIP = server
				+ "PromotionTripController/GetListPromotionTrip";
		public static final String GET_PAYMENT = server
				+ "PaymentController/GetDriverPayment/";
		public static final String DELETE_PAYMENT = server
				+ "PaymentController/DeleteDriverPayment";
		public static final String UPDATE_PROMOTION_TRIP_DETAILS = server
				+ "PromotionTripController/UpdatePromotionTripDetails";
		public static final String UPDATE_HOME_ADDRESS = server
				+ "DriverController/UpdateHomeAddress";
		public static final String RESET_PASSWORD = server
				+ "DriverController/ResetPassword";
		public static final String SEND_FEEDBACK = server
				+ "DriverController/SendFeedBack";
		public static final String UPLOAD_AVA = server + "FileUploadController/FileUploadAvatar";
	}

	public interface LOGIN_ERROR {
		public static final String WRONG_EMAIL_OR_PASSWORD = "WRONG_EMAIL_OR_PASSWORD";
		public static final String ACCOUNT_NOT_ACTIVE = "ACCOUNT_NOT_ACTIVE";
	}

	/*
	 * define String const
	 */

	public static final String SUCCESS = "SUCCESS";
	public static final String FAIL = "FAIL";
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
	public static final String STATUS = "status";
	public static final String ID = "id";
	public static final String RESPONSE_REQUEST = "RESPONSE_REQUEST";
	public static final String UPDATE_CURRENT_STATUS = "UPDATE_CURRENT_STATUS";
	public static final String TRIP = "TRIP";
	public static final String PROMOTION_TRIP = "PROMOTION_TRIP";
	public static final String RIDER = "RIDER";
	public static final String TNET_EMAIL = "hieudtse02895@gmail.com";

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
	}

	public interface RiderType {
		public static final String RegularRider = "RR";
		public static final String StrangerRider = "SR";
	}

	public interface Favorite {
		public static final String FavoriteRider = "FR";
		public static final String FavoriteDriver = "FD";
	}

	public interface LanguageCode {
		public static final String VI = "vi";
		public static final String EN = "en";
	}

	public interface PromotionTripRiderStatus {
		public static final String NEWREQUEST = "RG";
		public static final String ACCEPT = "AC";
		public static final String REJECT = "RJ";
		public static final String CANCEL = "CA";
	}

	public interface PromotionTripStatus {
		public static final String NEW_TRIP = "NT";
		public static final String OPEN = "OP";
		public static final String CANCEL = "CA";
	}

	public interface NotificationType {
		public static final String TRIP = "TRIP";
		public static final String PROMOTION_TRIP = "PROMOTION_TRIP";
	}

	public interface CardType {
		public static final String CR = "CR";
		public static final String VS = "VS";
		public static final String MC = "MC";
	}

	public interface UserStatus {
		public static final String ACTIVE = "AC";
		public static final String DEACTIVED = "DA";
		public static final String DELETED = "DE";
		public static final String BANED = "BA";
	}
}
