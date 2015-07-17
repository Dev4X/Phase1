package com.moonshot.dev4x.helpers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.moonshot.dev4x.models.*;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 4;//Initial database version
	private static final String DATABASE_NAME = "dev4xDb"; //Name of database
	String DEV4X_NODES_TABLE = "dev4x_nodes";
	String ID = "id";
	String NODE_NAME = "name";
	String NODE_ICON = "icon";
	String VIEW_COUNT = "view_count";
	String NODE_CONTENT = "content";
	String COUNT = "count";
	
	String DEV4X_CONTENT_CONSUMPTIONS = "dev4x_content_consumptions";
	String CONSUMPTION_ID = "cid";//Kind of session id for each video play
	String EVENT = "event";//Can be start, pause(stop), resume, complete, error
	String TIME = "time";//Time of event
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//Create tables.
		
		String metaDataSql = "CREATE TABLE "+DEV4X_NODES_TABLE+" (id INTEGER PRIMARY KEY, "+NODE_NAME+" TEXT, "+NODE_ICON+
						" TEXT, "+NODE_CONTENT+" TEXT, "+VIEW_COUNT+" INTEGER)";
		String cosumptionSql = "CREATE TABLE " + DEV4X_CONTENT_CONSUMPTIONS + " ( " + CONSUMPTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
				ID + " INTEGER, " + EVENT + " TEXT, " + TIME + " REAL)";
		db.execSQL(metaDataSql);
		db.execSQL(cosumptionSql);
		
		//Inserting dummy nodes values.
		
		ContentValues node1Values = new ContentValues();
		node1Values.put(ID, "1");
		node1Values.put(NODE_NAME, "VerbelSkills");
		node1Values.put(NODE_ICON, "node1");
		node1Values.put(NODE_CONTENT, "abc");
		node1Values.put(VIEW_COUNT, 0);
		db.insert(DEV4X_NODES_TABLE, null, node1Values);
		
		ContentValues node2Values = new ContentValues();
		node2Values.put(ID, "2");
		node2Values.put(NODE_NAME, "MathSkills");
		node2Values.put(NODE_ICON, "node2");
		node2Values.put(NODE_CONTENT, "abc");
		node2Values.put(VIEW_COUNT, 0);
		db.insert(DEV4X_NODES_TABLE, null, node2Values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + DEV4X_NODES_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DEV4X_CONTENT_CONSUMPTIONS);
		this.onCreate(db);
	}
	
	//Constructor function to create or connecte to database.
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	//Function to get all the skill nodes
	public List<Node> getAllNodes(){
		// Select All Query
		String selectQuery = "SELECT  * FROM " + DEV4X_NODES_TABLE;
		List<Node> nodeList = new ArrayList<Node>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Node node = new Node(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));
				nodeList.add(node);
			} while (cursor.moveToNext());
		}
		return nodeList;
	}
	
	public Node getNode(int nodeId){
		String selectQuery = "SELECT  * FROM " + DEV4X_NODES_TABLE+ " WHERE id = "+nodeId;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Node node = new Node(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));
				return node;
			} while (cursor.moveToNext());
		}
		return null;
	}
	
	public void increaseViewCountofContent(int nodeId){
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT  * FROM " + DEV4X_NODES_TABLE + " WHERE "+ ID + " = " + nodeId;
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			int currentCount = cursor.getInt(4);
			currentCount++;
			ContentValues values = new ContentValues();
			values.put(VIEW_COUNT, currentCount);
			db.update(DEV4X_NODES_TABLE, values, ID + " = ?",
					new String[] { String.valueOf(nodeId) });
			Log.v("VideoCount","VideoCount " + currentCount);
		}
	}
	
	public void createVideoConsumptionSessionEvent(int nodeId, String event){
		Log.v("VideoStatus","VideoStatus-" + event);
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ID, nodeId);
		values.put(EVENT, event);
		values.put(TIME, System.currentTimeMillis());
		db.insert(DEV4X_CONTENT_CONSUMPTIONS, null, values);
	}
}