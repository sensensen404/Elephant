package org.idaxiang.elephant.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.idaxiang.elephant.R;
import org.idaxiang.elephant.api.BaseApi;
import org.idaxiang.elephant.api.user.AccountApi;
import org.idaxiang.elephant.api.user.UserApi;
import org.idaxiang.elephant.model.AccountBean;
import org.idaxiang.elephant.model.UserModel;
import org.idaxiang.elephant.support.Utility;
import org.idaxiang.elephant.support.error.WeiboException;
import org.idaxiang.elephant.support.lib.MyAsyncTask;
import org.idaxiang.elephant.support.lib.sinasso.SsoHandler;
import org.idaxiang.elephant.ui.activity.ToolbarActivity;
import org.idaxiang.elephant.utils.AppLogger;

import java.lang.ref.WeakReference;

/**
 * User: qii
 * Date: 13-6-18
 */
public class SSOActivity extends ToolbarActivity {

    private SSOTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        getSupportActionBar().setTitle(R.string.official_app_login);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
        SsoHandler ssoHandler = new SsoHandler(SSOActivity.this);
        ssoHandler.authorize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.cancelTasks(task);
    }

    private static class SSOTask extends MyAsyncTask<String, UserModel, OAuthActivity.DBResult> {

        private WeakReference<SSOActivity> sSOActivityWeakReference;
        private WeiboException e;
        private OAuthActivity.ProgressFragment progressFragment = OAuthActivity.ProgressFragment
                .newInstance();
        private String token;
        private String expiresIn;

        public SSOTask(SSOActivity ssoActivity, String token, String expiresIn) {
            this.sSOActivityWeakReference = new WeakReference<SSOActivity>(ssoActivity);
            this.token = token;
            this.expiresIn = expiresIn;
        }

        @Override
        protected void onPreExecute() {
            progressFragment.setAsyncTask(this);

            SSOActivity activity = sSOActivityWeakReference.get();
            if (activity != null) {
                progressFragment.show(activity.getSupportFragmentManager(), "");
            }
        }

        @Override
        protected OAuthActivity.DBResult doInBackground(String... params) {
//            try {
//                UserModel user = new OAuthDao(token).getOAuthUserInfo();
                BaseApi.setAccessToken(token);
                String uid = AccountApi.getUid();
                UserModel user = UserApi.getUser(uid);
                AccountBean account = new AccountBean();
                account.setAccess_token(token);
                account.setExpires_time(
                        System.currentTimeMillis() + Long.valueOf(expiresIn) * 1000);
                account.setInfo(user);
                AppLogger
                        .e("token expires in " + Utility.calcTokenExpiresInDays(account) + " days");
//                return AccountDBTask.addOrUpdateAccount(account, false);
                return OAuthActivity.DBResult.add_successfuly;
//            } catch (WeiboException e) {
//                AppLogger.e(e.getError());
//                this.e = e;
//                cancel(true);
//                return null;
//            }
        }

        @Override
        protected void onCancelled(OAuthActivity.DBResult dbResult) {
            super.onCancelled(dbResult);
            if (progressFragment != null) {
                progressFragment.dismissAllowingStateLoss();
            }

            SSOActivity activity = sSOActivityWeakReference.get();
            if (activity == null) {
                return;
            }

            if (e != null) {
                Toast.makeText(activity, e.getError(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, R.string.you_cancel_login, Toast.LENGTH_SHORT).show();
            }
            activity.finish();
        }

        @Override
        protected void onPostExecute(OAuthActivity.DBResult dbResult) {
            if (progressFragment.isVisible()) {
                progressFragment.dismissAllowingStateLoss();
            }

            SSOActivity activity = sSOActivityWeakReference.get();
            if (activity == null) {
                return;
            }

            switch (dbResult) {
                case add_successfuly:
                    Bundle values = new Bundle();
                    values.putString("expires_in", expiresIn);
                    Intent intent = new Intent();
                    intent.putExtras(values);
                    activity.setResult(RESULT_OK, intent);
                    activity.finish();
                    Toast.makeText(activity, activity.getString(R.string.login_success),
                            Toast.LENGTH_SHORT).show();
                    break;
                case update_successfully:
                    Toast.makeText(activity, activity.getString(R.string.update_account_success),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            activity.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED || data == null) {
            Toast.makeText(this, R.string.you_cancel_login, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check OAuth 2.0/2.10 error code.
        String error = data.getStringExtra("error");
        if (error == null) {
            error = data.getStringExtra("error_type");
        }

        // error occurred.
        if (error != null) {
            if (error.equals("access_denied")
                    || error.equals("OAuthAccessDeniedException")) {
                Log.d("Weibo-authorize", "Login canceled by user.");
                Toast.makeText(this, R.string.you_cancel_login, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String description = data
                        .getStringExtra("error_description");
                if (description != null) {
                    error = error + ":" + description;
                }
                Log.d("Weibo-authorize", "Login failed: " + error);
                Toast.makeText(this, getString(R.string.login_failed) + error, Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
            return;
        }

        final String KEY_TOKEN = "access_token";
        final String KEY_EXPIRES = "expires_in";
        final String KEY_REFRESHTOKEN = "refresh_token";

        String token = data.getStringExtra(KEY_TOKEN);
        String expires = data
                .getStringExtra(KEY_EXPIRES);
        String refreshToken = data.getStringExtra(KEY_REFRESHTOKEN);

        if (Utility.isTaskStopped(task)) {
            task = new SSOTask(SSOActivity.this, token, expires);
            task.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
