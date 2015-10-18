//package com.example.garrisonthomas.onewordday;
//
//import android.content.res.TypedArray;
//import android.support.v4.widget.SwipeRefreshLayout;
//
//import com.parse.FindCallback;
//import com.parse.ParseQuery;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by GarrisonThomas on 2015-10-17.
// */
//public class PopulateRecyclerView {
//
//    public static List<RecyclerViewData> getData(String todaysDate, final TypedArray moodImages, final SwipeRefreshLayout swipeRefreshLayout) {
//
//        final List<RecyclerViewData> data = new ArrayList<>();
//        final ParseQuery<DailyWord> query = new ParseQuery<>("DailyWord");
//        query.whereEqualTo("date", todaysDate);
//        query.orderByDescending("createdAt");
//        query.findInBackground(new FindCallback<DailyWord>() {
//            public void done(List<DailyWord> objects, com.parse.ParseException e) {
//
//                if (e == null) {
//                    for (DailyWord newWord : objects) {
//                        RecyclerViewData current = new RecyclerViewData();
//
//                        current.iconId = moodImages.getResourceId(newWord.getMood(), 0);
//                        current.title = newWord.toString();
//                        current.userAndDate = "Submitted by " + newWord.getRelatedUsername() + " on " + newWord.getDate();
//                        data.add(current);
//                    }
//                }
//                if (swipeRefreshLayout.isRefreshing()) {
//                    swipeRefreshLayout.setRefreshing(false);
//                }
//            }
//        });
//        return data;
//    }
//
//}
