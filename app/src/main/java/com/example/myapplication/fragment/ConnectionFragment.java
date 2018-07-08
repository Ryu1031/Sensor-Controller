package com.example.myapplication.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.SystemApplication;
import com.example.myapplication.websocket.WebSocket;

import org.java_websocket.WebSocketClient;

import utils.AppUtil;
import utils.Log;

/**
 * Created by Ryusuke on 2016/08/11.
 */
public class ConnectionFragment extends Fragment {
    public static final String FRAGMENT_TAG = ConnectionFragment.class.getSimpleName();

    private Context mContext;

    private LoadTask mLoadTask;

    private UriConfigDialogFragment mUriConfigDialogFragment;

    private WebSocketClient mWebSocketClient;

    public static ConnectionFragment newInstance() {
        Bundle args = new Bundle();
        ConnectionFragment fragment = new ConnectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("onCreateView");
        mContext = getActivity().getApplicationContext();
        SystemApplication application = (SystemApplication) getActivity().getApplication();
        mWebSocketClient = application.getWebSocketClient();
        return inflater.inflate(R.layout.connection_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart");
        showDialogFragment();
    }

    private void relese() {
        if (mLoadTask != null && mLoadTask.isCancelled()) {
            mLoadTask.cancel(false);
            mLoadTask = null;
        }
    }

    private void showFragment() {
        Fragment fragment = ControlFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, ControlFragment.FRAGMENT_TAG);
        transaction.commit();
    }

    private void showDialogFragment() {
        mUriConfigDialogFragment = UriConfigDialogFragment.newInstance();
        mUriConfigDialogFragment.setOnCancelClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                relese();
                getActivity().finish();
            }
        });
        mUriConfigDialogFragment.setSubmitButtonCallback(
                new UriConfigDialogFragment.SubmitButtonCallback() {
                    @Override
                    public void onSubmitButtonCallback(String ip) {
                        startLoadTask(ip);
                    }
                });
        mUriConfigDialogFragment.setCancelable(false);
        mUriConfigDialogFragment
                .show(getFragmentManager(), UriConfigDialogFragment.FRAGMENT_TAG);
    }

    private void startLoadTask(String entry) {
        mLoadTask = new LoadTask(getActivity(), entry);
        mLoadTask.execute();
    }

    private class LoadTask extends AsyncTask<Void, WebSocketClient, WebSocketClient> {
        private final Activity mActivity;

        private final String mIpAddress;

        private ProgressDialog mProgressDialog;

        public LoadTask(Activity activity, String ipAddress) {
            mActivity = activity;
            mIpAddress = ipAddress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute");
            showProgressDialog();
        }

        @Override
        protected WebSocketClient doInBackground(Void... voids) {
            Log.d("doInBackground:" + mIpAddress);
            WebSocket webSocket = new WebSocket(mIpAddress);
            webSocket.setOnCallbackListener(new WebSocket.WebScoketCallbackListener() {
                @Override
                public void onOpen(final Short httpState) {
                    Log.d("onOpen:" + httpState);
                    mProgressDialog.dismiss();
                    relese();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (httpState) {
                                case 101:
                                    mUriConfigDialogFragment.dismiss();
                                    AppUtil.showToast(mContext, "onOpen:" + httpState);
                                    showFragment();
                                    break;
                                default:
                                    AppUtil.showToast(mContext, "Connection open:" + httpState);
                                    break;
                            }
                        }
                    });

                }

                @Override
                public void onMessage(String message) {
                    Log.d("onMessage");
                    mProgressDialog.dismiss();
                }

                @Override
                public void onClose(final int code, String reason, boolean remote) {
                    Log.d("onClose");
                    mProgressDialog.dismiss();
                    relese();
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (code) {
                                    case -1:
                                        AppUtil.showToast(mContext, "Connection fail:" + code);
                                        //TODO:
//                                    mProgressDialog.dismiss();
//                                    mUriConfigDialogFragment.dismiss();
//                                    showFragment();
                                        break;
                                    default:
                                        AppUtil.showToast(mContext, "Connection close:" + code);
                                        break;
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.w("onError:" + e);
                }
            });
            WebSocketClient client = null;
            while (client == null) {
                client = webSocket.setup();
                if (client != null) {
                    break;
                }
            }


            client.connect();
            return client;
        }

        @Override
        protected void onPostExecute(WebSocketClient webSocketClient) {
            Log.d("onPostExecute:" + webSocketClient);
            super.onPostExecute(webSocketClient);
            SystemApplication application = (SystemApplication) getActivity().getApplication();
            application.setWebSocketClient(webSocketClient);
            mWebSocketClient = webSocketClient;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mProgressDialog != null || mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        private void showProgressDialog() {
            mProgressDialog = new ProgressDialog(mActivity);
            String message = mIpAddress + "\n" + getString(R.string.progress_dialog_message);
            mProgressDialog.setMessage(message);
            mProgressDialog.setButton(getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            cancel(true);
                            mWebSocketClient.close();
                        }
                    });
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    mWebSocketClient.close();
                }
            });
            mProgressDialog.show();
        }
    }

}
