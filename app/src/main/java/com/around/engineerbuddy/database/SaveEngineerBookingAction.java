package com.around.engineerbuddy.database;

import android.content.Context;
import android.os.AsyncTask;

public class SaveEngineerBookingAction {
    Context context;
    EOEngineerBookingInfo eoEngineerBookingInfo;

    public SaveEngineerBookingAction(Context context, EOEngineerBookingInfo eoEngineerBookingInfo) {
        this.context = context;
        this.eoEngineerBookingInfo = eoEngineerBookingInfo;
        SaveTask st = new SaveTask();
        st.execute();
    }

    public static void saveInfo(SaveTask st) {
        // SaveTask st = new SaveTask();
        st.execute();
    }


    class SaveTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            //creating a task
            EOEngineerBookingInfo saveEngineerBookingInfo = new EOEngineerBookingInfo();
//            saveEngineerBookingInfo.setEngineerLoginID(eoEngineerBookingInfo.getEngineerLoginID());
//            saveEngineerBookingInfo.setBookingID(eoEngineerBookingInfo.getBookingID());
//            saveEngineerBookingInfo.setEngineerLocation(eoEngineerBookingInfo.getEngineerLocation());
            saveEngineerBookingInfo.setActionNameOverBooking(eoEngineerBookingInfo.getActionNameOverBooking());
            saveEngineerBookingInfo.setBookingInfoOfEngineerAction(eoEngineerBookingInfo.getBookingInfoOfEngineerAction());
            saveEngineerBookingInfo.setFinished(false);

            //adding to database
            DataBaseClient.getInstance(context.getApplicationContext()).getAppDatabase()
                    .engineerBookingInfoDao()
                    .insert(saveEngineerBookingInfo);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            finish();
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
        }


    }
}
