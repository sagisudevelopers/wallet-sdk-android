package com.sagisu.vault.ui.contacts;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNFetchMessageItem;
import com.pubnub.api.models.consumer.history.PNFetchMessagesResult;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadata;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.channel.PNGetChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.channel.PNSetChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;
import com.sagisu.vault.R;
import com.sagisu.vault.models.MessageBean;
import com.sagisu.vault.network.ApiClient;
import com.sagisu.vault.ui.login.fragments.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PubnubViewModel extends ViewModel {

    PNConfiguration pnConfiguration;

    PubNub pubnub;
    String channelName;
    User user;
    String recipientName;
    String uid;
    private MutableLiveData<List<MessageBean>> mMessageBeanList = new MutableLiveData<>();
    private MutableLiveData<Boolean> clearChatBox = new MutableLiveData<>();

    public MutableLiveData<List<MessageBean>> getmMessageBeanList() {
        return mMessageBeanList;
    }

    public MutableLiveData<Boolean> getClearChatBox() {
        return clearChatBox;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName.substring(1);
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void initPubNubClient() {
        uid = user.getPhone().substring(1);
        pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(ApiClient.PUBNUB_SUB_KEY);
        pnConfiguration.setPublishKey(ApiClient.PUBNUB_PUB_KEY);
        pnConfiguration.setUuid(uid);

        pubnub = new PubNub(pnConfiguration);

        //setChannelMetadata();
        //getChannelMetadata(channelName);
        fetchMessages();
        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(@NotNull PubNub pubnub, @NotNull PNStatus pnStatus) {
                if (pnStatus.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // This event happens when radio / connectivity is lost
                } else if (pnStatus.getCategory() == PNStatusCategory.PNConnectedCategory) {

                    // Connect event. You can do stuff like publish, and know you'll get it.
                    // Or just use the connected event to confirm you are subscribed for
                    // UI / internal notifications, etc

                    if (pnStatus.getCategory() == PNStatusCategory.PNConnectedCategory) {

                    }
                } else if (pnStatus.getCategory() == PNStatusCategory.PNReconnectedCategory) {

                    // Happens as part of our regular operation. This event happens when
                    // radio / connectivity is lost, then regained.
                } else if (pnStatus.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {

                    // Handle messsage decryption error. Probably client configured to
                    // encrypt messages and on live data feed it received plain text.
                }
            }

            @Override
            public void message(@NotNull PubNub pubnub, @NotNull PNMessageResult pnMessageResult) {
                // Handle new message stored in message.message
                if (pnMessageResult.getChannel() != null) {
                    // Message has been received on channel group stored in
                    // message.getChannel()
                } else {
                    // Message has been received on channel stored in
                    // message.getSubscription()
                }
                Log.d("TAG", "publisher: " + pnMessageResult.getPublisher());
                // extract desired parts of the payload, using Gson
                final String msg = pnMessageResult.getMessage().getAsJsonObject().get("msg").getAsString();
                final long timestamp = pnMessageResult.getMessage().getAsJsonObject().get("timestamp").getAsLong();
                final String paymentId = pnMessageResult.getMessage().getAsJsonObject().get("paymentId").getAsString();
                if (!pnMessageResult.getPublisher().equals(pnConfiguration.getUuid())) {

                    //String account = pnMessageResult.getPublisher();
                    //Log.i("TAG", "onMessageReceived account = " + account + " msg = " + msg);
                    MessageBean messageBean = new MessageBean(recipientName, msg, timestamp, paymentId, false);
                    messageBean.setBackground(R.drawable.circle_shape_blue);
                    List<MessageBean> tmpList = mMessageBeanList.getValue();
                    tmpList.add(messageBean);
                    mMessageBeanList.setValue(tmpList);
                           /* mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                            mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);*/

                }

            /*
                log the following items with your favorite logger
                    - message.getMessage()
                    - message.getSubscription()
                    - message.getTimetoken()
            */
            }

            @Override
            public void presence(@NotNull PubNub pubnub, @NotNull PNPresenceEventResult pnPresenceEventResult) {

            }

            @Override
            public void signal(@NotNull PubNub pubnub, @NotNull PNSignalResult pnSignalResult) {

            }

            @Override
            public void uuid(@NotNull PubNub pubnub, @NotNull PNUUIDMetadataResult pnUUIDMetadataResult) {

            }

            @Override
            public void channel(@NotNull PubNub pubnub, @NotNull PNChannelMetadataResult pnChannelMetadataResult) {

            }

            @Override
            public void membership(@NotNull PubNub pubnub, @NotNull PNMembershipResult pnMembershipResult) {

            }

            @Override
            public void messageAction(@NotNull PubNub pubnub, @NotNull PNMessageActionResult pnMessageActionResult) {

            }

            @Override
            public void file(@NotNull PubNub pubnub, @NotNull PNFileEventResult pnFileEventResult) {

            }
        });

        pubnub.subscribe().channels(Arrays.asList(user.getPhone())).execute();

    }

    public void sendMessage(String message, String paymentId) {
        MessageBean messageBean = new MessageBean(user.getFullName(), message, System.currentTimeMillis()/1000, paymentId, true);
        List<MessageBean> tmpList = mMessageBeanList.getValue();
        tmpList.add(messageBean);
        mMessageBeanList.setValue(tmpList);
        /*mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
        mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);*/

        // create message payload using Gson
        final JsonObject messageJsonObject = new JsonObject();
        messageJsonObject.addProperty("msg", message);
        messageJsonObject.addProperty("timestamp", messageBean.getTimestamp());
        messageJsonObject.addProperty("paymentId", messageBean.getPaymentId());

        pubnub.publish().channel(channelName).message(messageJsonObject).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                // Check whether request successfully completed or not.
                if (!status.isError()) {
                    clearChatBox.setValue(true);
                    // Message successfully published to specified channel.
                }
                // Request processing failed.
                else {
                    // Handle message publish error. Check 'category' property to find out possible issue
                    // because of which request did fail.
                    //
                    // Request can be resent using: [status retry];
                }
            }
        });
    }

    public void fetchMessages() {
        String uids[] = new String[] {channelName, uid};
        pubnub.fetchMessages()
                .channels(Arrays.asList(uids))
                .async(new PNCallback<PNFetchMessagesResult>() {
                    @Override
                    public void onResponse(@Nullable final PNFetchMessagesResult result, @NotNull final PNStatus status) {
                        if (!status.isError()) {
                            final Map<String, List<PNFetchMessageItem>> channelToMessageItemsMap = result.getChannels();
                            final Set<String> channels = channelToMessageItemsMap.keySet();
                            List<MessageBean> tmpList = new ArrayList<>();
                            for (final String channel : channels) {
                                List<PNFetchMessageItem> pnFetchMessageItems = channelToMessageItemsMap.get(channel);
                                for (final PNFetchMessageItem fetchMessageItem : pnFetchMessageItems) {
                                    if(!Arrays.asList(uids).contains(fetchMessageItem.getUuid())) continue;
                                    final String msg = fetchMessageItem.getMessage().getAsJsonObject().get("msg").getAsString();
                                    final long timestamp = fetchMessageItem.getMessage().getAsJsonObject().get("timestamp").getAsLong();
                                    String paymentId = null;
                                    if (fetchMessageItem.getMessage().getAsJsonObject().has("paymentId"))
                                        paymentId = fetchMessageItem.getMessage().getAsJsonObject().get("paymentId").getAsString();
                                    boolean beSelf = fetchMessageItem.getUuid().equals(pnConfiguration.getUuid());
                                    String account = beSelf ? user.getFullName() : recipientName;
                                    Log.i("TAG", "onMessageReceived account = " + account + " msg = " + timestamp);
                                    MessageBean messageBean = new MessageBean(account, msg, timestamp, paymentId, beSelf);
                                    messageBean.setBackground(R.drawable.circle_shape_blue);
                                    tmpList.add(messageBean);
                           /* mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                            mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);*/
                                }
                            }
                            mMessageBeanList.setValue(tmpList);
                        } else {
                            mMessageBeanList.setValue(new ArrayList<>());
                            System.err.println("Handling error" + status.getErrorData());
                        }
                    }
                });
    }

    private void setChannelMetadata() {
        Map<String, Object> custom = new HashMap<>();
        custom.put("owner", user.getFullName());
        pubnub.setChannelMetadata()
                .channel(user.getPhone())
                .name(user.getFullName())
                .custom(custom)
                .includeCustom(true)
                .async(new PNCallback<PNSetChannelMetadataResult>() {
                    @Override
                    public void onResponse(@Nullable final PNSetChannelMetadataResult result, @NotNull final PNStatus status) {
                        if (status.isError()) {
                            //handle error
                        } else {
                            //handle result
                        }
                    }
                });
    }

    private void getChannelMetadata(String channelName) {
        pubnub.getChannelMetadata()
                .channel(channelName)
                .includeCustom(true)
                .async(new PNCallback<PNGetChannelMetadataResult>() {
                    @Override
                    public void onResponse(@Nullable final PNGetChannelMetadataResult result, @NotNull final PNStatus status) {
                        if (status.isError()) {
                            //handle error
                        } else {
                            //handle result
                            PNChannelMetadata pnChannelMetadata = result.getData();
                            recipientName = pnChannelMetadata.getName();
                        }
                    }
                });
    }

}