package com.inthree.boon.deliveryapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.request.LoginReq;
import com.inthree.boon.deliveryapp.response.LoginResp;
import com.inthree.boon.deliveryapp.server.rest.InthreeApi;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.inthree.boon.deliveryapp.app.Constants.ApiHeaders.BASE_URL;

public class ExternalDbOpenHelper extends SQLiteOpenHelper {


    public static String DB_PATH;

    public static String DB_NAME;
    public SQLiteDatabase database;
    public final Context context;

    public static final String KEY_BoxId = "bxid";
    public static final String KEY_DataTime = "orderid";
    public static final String KEY_PName = "pname";
    public static final String KEY_Address = "addresskyc";
    public static final String KEY_Status = "status";


    private ArrayList<LoginResp> loginList;

    //private static final String KEY_QTY = "qty";

    public SQLiteDatabase getDb() {
        Log.v("check_dbcall", "- "+ "getDb");
        return database;
    }

    public ExternalDbOpenHelper(Context context, String databaseName) {
        super(context, databaseName, null, Constants.DB_VERSION);
        this.context = context;

        String packageName = context.getPackageName();

//        DB_PATH = String.format("//data//data//%s//databases//", packageName);
        DB_PATH = context.getDatabasePath(databaseName).getPath();

        DB_NAME = databaseName;

//        this.getReadableDatabase();
//        this.close();

        openDataBase();
    }

    private static final String NONPRODUCTIVEMASTER_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + "NonProductiveMaster" + " (" +
                    "sno" + " INTEGER PRIMARY KEY  NOT NULL , " +
                    "reason_id" + " TEXT," +
                    "reason" + " TEXT" +
                    ");";


    private static final String FEEDBACK =
            "CREATE TABLE IF NOT EXISTS  " + "FeedBackMaster" + "(" +
                    "fid" + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , " +
                    "qid" + " TEXT, " +
                    "que_type" + " TEXT, " +
                    "que_answer" + " TEXT, " +
                    "question" + " TEXT, " +
                    "status" + " TEXT)";

    private static final String LANGUAGE_MASTER = "CREATE TABLE IF NOT EXISTS LanguageMaster (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , language TEXT, language_id INTEGER, is_active INTEGER)";


    private static final String BFILBranchMaster = "CREATE TABLE IF NOT EXISTS BranchMaster (b_id INTEGER PRIMARY KEY  NOT NULL  UNIQUE , branch_id TEXT, branch_name TEXT, status TEXT)";
    private static final String ServiceIncompleteConfirmation = "CREATE TABLE IF NOT EXISTS ServiceIncompleteConfirmation (sid INTEGER PRIMARY KEY  NOT NULL  UNIQUE , ship_no TEXT, reason TEXT, reason_status TEXT, incom_long TEXT, incom_lat TEXT)";
    private static final String ServiceIncompleteReasonMaster = "CREATE TABLE IF NOT EXISTS ServiceIncompleteReasonMaster (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , rid INTEGER, reason TEXT, reasonstatus TEXT)";
    private static final String serviceConfirmation = "CREATE TABLE IF NOT EXISTS serviceConfirmation (id INTEGER PRIMARY KEY  NOT NULL ,ship_num TEXT,customer_fname TEXT,customer_cnum TEXT,ship_address TEXT,ship_city TEXT,ship_phone TEXT,customer_feedback TEXT,created_date DATETIME,function TEXT,documents TEXT,feedback TEXT DEFAULT (null) , signProof TEXT)";
    private static final String serviceFeedbackItems = "CREATE TABLE IF NOT EXISTS serviceFeedbackItems (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , feedback TEXT, feedback_status TEXT, feedback_id INTEGER, shipment_no TEXT)";
    private static final String serviceItems = "CREATE TABLE IF NOT EXISTS serviceItems (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , service_id INTEGER, sku TEXT, name TEXT, qty INTEGER, item_received INTEGER, qty_demo_completed INTEGER, order_item_id INTEGER, product_serial_no TEXT, product_type INTEGER, created_at DATETIME, shipment_number TEXT)";
    private static final String serviceMaster = "CREATE TABLE IF NOT EXISTS serviceMaster (id INTEGER PRIMARY KEY  NOT NULL ,reference TEXT,order_id TEXT,shipment_id TEXT,customer_name TEXT,customer_contact_number TEXT,alternate_contact_number TEXT,shipping_address TEXT,shipping_city TEXT,shipping_pincode TEXT,shipping_telephone TEXT,cityCode TEXT,lmp_code TEXT,agent_id INTEGER,sync_status TEXT DEFAULT (null) ,assigned_at DATETIME,received_at DATETIME,download_sync DATETIME,dio_status INTEGER,attempt INTEGER,reason TEXT,created_at DATETIME, delivery_status TEXT, image_status TEXT)";
    private static final String serviceProductAttributes = "CREATE TABLE IF NOT EXISTS serviceProductAttributes (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , attribute_id INTEGER, attribute_name TEXT, product_type INTEGER, shipment_no TEXT, checked INTEGER, input_field_type TEXT, is_require INTEGER, attribute_type TEXT, text_content TEXT)";
    private static final String pickupConfirmation = "CREATE TABLE IF NOT EXISTS PickupConfirmation (id INTEGER PRIMARY KEY  NOT NULL ,orderno TEXT,shipmentno TEXT,customername TEXT,customerphone TEXT,customeraddress TEXT,customerphoto TEXT,pickup_completed INTEGER,pickupstatus TEXT,createdate DATETIME,latitude TEXT DEFAULT (null) ,longitude TEXT, reason TEXT, reason_id INTEGER)";




/*	public void createDataBase() {

		boolean dbExist = checkDataBase();
		if (!dbExist) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				Log.e(this.getClass().toString(), "Copying error");
				throw new Error("Error copying database!");
			}
		} else {
			Log.i(this.getClass().toString(), "Database already exists");
		}
	}*/

    public void createDataBase() {

	/*	try {
			copyDataBase();
		} catch (IOException e) {
			Log.e(this.getClass().toString(), "Copying error");
			throw new Error("Error copying database!");
		}*/

        boolean dbExist = checkDataBase();
        Log.v("createDataBase"," - "+dbExist);
        if (!dbExist) {
            this.getReadableDatabase();
            this.close();

            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Copying error");
                throw new Error("Error copying database!");
            }
        } else {
//            this.setWriteAheadLoggingEnabled(false);
            Log.i(this.getClass().toString(), "Database already exists");
            Log.v("getReadableDatabase", "getReadableDatabase1");
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDb = null;
        try {

//            String path = DB_PATH + DB_NAME;
            String path = DB_PATH;
//            checkDb = SQLiteDatabase.openDatabase(path, null,
//                    SQLiteDatabase.OPEN_READONLY);
            File file = new File(DB_PATH);
            if (file.exists() && !file.isDirectory())
                checkDb = SQLiteDatabase.openDatabase(path, null,
                        SQLiteDatabase.OPEN_READWRITE);
            Log.v("checkDataBase"," - "+checkDb);

        } catch (SQLException e) {
            Log.e(this.getClass().toString(), "Error while checking db");
        }

        if (checkDb != null) {
            checkDb.close();
        }
        return checkDb != null;
    }

    private void copyDataBase() throws IOException {

        InputStream externalDbStream = context.getAssets().open(DB_NAME);
        Log.v("copyDataBase","- "+ externalDbStream);

//        String outFileName = DB_PATH + DB_NAME;
        String outFileName = DB_PATH;

//        String outFileName = DB_PATH + DB_NAME;
        Log.v("copyDataBase"," - "+outFileName);


        OutputStream localDbStream = new FileOutputStream(outFileName);


        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = externalDbStream.read(buffer)) > 0) {
            localDbStream.write(buffer, 0, bytesRead);
        }

        localDbStream.close();
        externalDbStream.close();

    }

    public SQLiteDatabase openDataBase() throws SQLException {

//        String path = DB_PATH + DB_NAME;
        String path = DB_PATH;

        if (database == null) {
            Log.v("openDataBase"," - "+database);
            createDataBase();
            Log.v("check_dbcall", "- "+ "openDataBase");
            database = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READWRITE);

        }
        return this.getWritableDatabase();

    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("onCreate_db","- "+ "onCreate_db");
    }
	/*@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


	/* Method to create a Employee
	 public long createProduct(AtomPayment pro) {
	     long c =0;

	     //SQLiteDatabase database = getWritableDatabase();
	     ContentValues values = new ContentValues();
	     values.put(KEY_BoxId, pro.getOrderID());
	     values.put(KEY_DataTime, pro.getDod());
	     values.put(KEY_PName, pro.getProductName());
	     values.put(KEY_Address, pro.getAddress());
	     values.put(KEY_Status, pro.getStatus());
	     //values.put(KEY_ADDRESS, emp.getAddress());
	     // c = database.insert(TABLE_EMP, null, values);
	     // database.close();
	     return c;

	 }*/

    /* Method for fetching record from Database */ /* This method is used to get a single record from Database.
	    I have given an example, you have to do something like this. */

    private Context getBaseContext() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS RunningStatus");

        db.execSQL(FEEDBACK);
        db.execSQL(LANGUAGE_MASTER);
        db.execSQL(ServiceIncompleteConfirmation);
        db.execSQL(ServiceIncompleteReasonMaster);
        db.execSQL(serviceConfirmation);
        db.execSQL(serviceFeedbackItems);
        db.execSQL(serviceItems);
        db.execSQL(serviceMaster);
        db.execSQL(serviceProductAttributes);
        db.execSQL(pickupConfirmation);
        db.execSQL(BFILBranchMaster);

        onCreate(db);
        Log.v("onupgrade", "upgrade");
//		 db.execSQL(NONPRODUCTIVEMASTER_TABLE_CREATE);





        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN return_id TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN pickup_type TEXT ");
        } catch (Exception Exp) {
        }


        try {
            db.execSQL("ALTER TABLE PageTracker ADD COLUMN device_info TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE PageTracker ADD COLUMN battery TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE UserMaster ADD COLUMN aadhaar INTEGER ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE DeliveryConfirmation ADD COLUMN feed_back TEXT ");
        } catch (Exception Exp) {
        }



        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN otp TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN urn TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN tamil TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN telugu TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN punjabi TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN hindi TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN bengali TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN kannada TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN assam TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN orissa TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN marathi TEXT ");
        } catch (Exception Exp) {
        }


        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN tamil TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN telugu TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN punjabi TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN hindi TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN bengali TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN kannada TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN assam TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN orissa TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN marathi TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ReasonMaster ADD COLUMN tamil TEXT ");
        } catch (Exception Exp) {
        }try {
            db.execSQL("ALTER TABLE ReasonMaster ADD COLUMN telugu TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ReasonMaster ADD COLUMN punjabi TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ReasonMaster ADD COLUMN hindi TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ReasonMaster ADD COLUMN bengali TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ReasonMaster ADD COLUMN kannada TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ReasonMaster ADD COLUMN assam TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ReasonMaster ADD COLUMN orissa TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ReasonMaster ADD COLUMN marathi TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN partial_reason TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE UserMaster ADD COLUMN user_role INTEGER ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN other_partial_reason TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ProductDetails ADD COLUMN r_id INTEGER ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE ReasonMaster ADD COLUMN reason_type INTEGER ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE UndeliveredConfirmation ADD COLUMN reason_id INTEGER ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN order_type INTEGER ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN pickup_status TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN max_attempt INTEGER ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE DeliveryConfirmation ADD COLUMN verify TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE DeliveryConfirmation ADD COLUMN neft TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN delivery_aadhar_required INTEGER DEFAULT 0 ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE DeliveryConfirmation ADD COLUMN aadhar_voter_type TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN virtual_id TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE UserMaster ADD COLUMN user_role INTEGER ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE DeliveryConfirmation ADD COLUMN relation_proof TEXT ");
        } catch (Exception Exp) {
        }
        try {
            db.execSQL("ALTER TABLE DeliveryConfirmation ADD COLUMN received_by TEXT ");
        } catch (Exception Exp) {
        }


        /*bfil upgrade*/

        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN branch_code TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN invoice_id TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN delivery_to TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN check_bfil_order_status TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN invoice_date TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN invoice_status TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN return_id TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE orderheader ADD COLUMN loanrefno TEXT ");
        } catch (Exception Exp) {
        }

        try {
            db.execSQL("ALTER TABLE DeliveryConfirmation ADD COLUMN bulk_Shipment_append TEXT ");
        } catch (Exception Exp) {
        }




        getUserAccess(db);

    }

    public SQLiteDatabase deleteDatabase() throws SQLException {
//        String path = DB_PATH + DB_NAME;
        String path = DB_PATH;
        if (database == null) {
            createDataBase();
            database = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READWRITE);
            SQLiteDatabase.deleteDatabase(new File(path));
        }
        return database;
    }

    public void backup(String outFileName) {

        //database path
        final String inFileName = context.getDatabasePath(DB_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(context, "Backup Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, "Unable to backup database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


//    @Override
//    public void onOpen(SQLiteDatabase db) {
//        super.onOpen(db);
////        db.disableWriteAheadLogging();
//    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
//        super.onConfigure(db);
        super.onOpen(db);
        db.rawQuery("PRAGMA journal_mode = OFF",null);
        db.disableWriteAheadLogging();
    }


    /**
     * Need to hit the api user login for credential to download the product
     */


    /*  *
     * Call the webservice for check the username and password for login
     **/

    public void getUserAccess(final SQLiteDatabase database) {




        String user_name = null;
        String pass_word = null;

        Cursor uname = database.rawQuery("Select IFNULL(username,0) as username,IFNULL(password,0) as password from UserMaster ", null);

        if(uname.getCount()>0 && uname!=null){
            uname.moveToFirst();
            user_name=uname.getString(uname.getColumnIndex("username"));
            pass_word=uname.getString(uname.getColumnIndex("password"));
            uname.close();
        }

        String getFirebaseId = AppController.getStringPreference(Constants.DEVICE_TOKEN_REGID, "");
        String getDeviceModel = AppController.getStringPreference(Constants.DEVICE, "");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);
        LoginReq login = new LoginReq();
        JSONObject paramObject = null;
        login.setUsername(user_name);
        login.setPassword(pass_word);
        try {
            paramObject = new JSONObject();
            paramObject.put("username", login.getUsername());
            paramObject.put("password", login.getPassword());
            paramObject.put("firebase_id", getFirebaseId); // updating firebase regid during login
            paramObject.put("device_info", getDeviceModel); // updating device model during login


            Log.v("getUserAccess", getFirebaseId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

        final Observable<LoginResp> observable = apiService.getLogin(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<LoginResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginResp value) {
                loginList = value.getLogin();

                if (value.getResMsg().equals("login success")) {
//                    Log.v("get_response", value.getResMsg());
                    for (int i = 0; i < loginList.size(); i++) {
//                        Log.v("login_user", loginList.get(i).getId());
                        database.rawQuery("Delete from UserMaster ", null);

                        String query = "INSERT INTO UserMaster (user_id, username, password,firstname,lastname,Email,Mobile,status,aadhaar,user_role ) " +
                                "VALUES ('" + loginList.get(i).getId() + "','" + loginList.get(i).getUsername() + "','" +
                                loginList.get(i).getPassword() + "','" + loginList.get(i).getFirstname() + "'," +
                                "'" + loginList.get(i).getLastname() + "','" + loginList.get(i).getEmail() + "','" +
                                loginList.get(i).getMobileNo() + "','O'," + loginList.get(i).getAadhaar_feature() + "," + loginList.get(i).getRole() + ")";
                        database.execSQL(query);

                        for(int j = 0; j < loginList.get(i).getLanguageArray().size(); j++){
                            Log.v("getLanguageArray",loginList.get(i).getLanguageArray().get(j).getLanguage());

                            Cursor uname = database.rawQuery("Select * from LanguageMaster where language_id = '" + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "' ", null);

                            if (uname.getCount() == 0) {
                                uname.moveToFirst();
                                String insertUndeliveredReason = "Insert into LanguageMaster (language ,language_id,is_active) Values('" + loginList.get(i).getLanguageArray().get(j).getLanguage() + "', " + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "" +
                                        "," + loginList.get(i).getLanguageArray().get(j).getLanguage_active() + ")";
                                database.execSQL(insertUndeliveredReason);
                            }else {
                                String updateUndeliveredReason = "Update LanguageMaster set language_id = " + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "," +
                                        "language = '" + loginList.get(i).getLanguageArray().get(j).getLanguage() + "', is_active ='" + loginList.get(i).getLanguageArray().get(j).getLanguage_active() + "' where language_id = '" + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "'";
                                database.execSQL(updateUndeliveredReason);
                            }
                        }

                        AppController.storeStringPreferences(Constants.LOGIN_USER_ID, loginList.get(i).getId());
                        AppController.storeStringPreferences(Constants.LOGIN_USER_EMAIL, loginList.get(i).getEmail());
                        AppController.storeStringPreferences(Constants.USER_NAME, loginList.get(i).getUsername());
                        AppController.storeStringPreferences(Constants.ROLE_ID, loginList.get(i).getRole());
                        AppController.setLocale("en");


//                        finish();

                    }
                } else if (value.getResMsg().equals("login failed")) {
                    Logger.showShortMessage(context, context.getResources().getString(R.string.invalid_login));

                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("loginerror", e.toString());
                Logger.showShortMessage(context, "Network Error");

            }

            @Override
            public void onComplete() {
//                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }


}
