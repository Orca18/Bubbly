package com.mainnet.bubbly.tabFragments;

import androidx.fragment.app.Fragment;

public class Fragment_Tab1_follower extends Fragment {
//
//    View v;
//    String uid;
//
//    Follower_Adapter follower_adapter;
//    ArrayList<follower_Response> followerList;
//    LinearLayoutManager linearLayoutManager;
//    RecyclerView recyclerView;
//    private Parcelable recyclerViewState;
//    SharedPreferences preferences;
//    String user_id;
//
//    public Fragment_Tab1_follower() {
//        // Required empty public constructor
//    }
//
//    // TODO: Rename and change types and number of parameters
//    public static Fragment_Tab1_follower newInstance(String param1, String param2) {
//        Fragment_Tab1_follower fragment = new Fragment_Tab1_follower();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        uid = ((Following) getActivity()).getUid();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        v = inflater.inflate(R.layout.fragment_profile_tab1, container, false);
//
//        recyclerView = v.findViewById(R.id.tab_recyclerview);
//        preferences = getActivity().getSharedPreferences("novarand",MODE_PRIVATE);
//
//        selectFollowerList();
//
//        return v;
//    }
//
//    public void selectFollowerList(){
//        linearLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        //위치 유지
//        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
//        //위치 유지
//        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
//
//        followerList = new ArrayList<>();
//        follower_adapter = new Follower_Adapter(getActivity().getApplicationContext() , followerList,getActivity().getApplicationContext() );
//        recyclerView.setAdapter(follower_adapter);
//        follower_adapter.notifyDataSetChanged();
//        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
//
//        ApiInterface selectFollowerList_api = ApiClient.getApiClient().create(ApiInterface.class);
//        Call<List<follower_Response>> call = selectFollowerList_api.selectFollowerList(user_id);
//        call.enqueue(new Callback<List<follower_Response>>()
//        {
//            @Override
//            public void onResponse(@NonNull Call<List<follower_Response>> call, @NonNull Response<List<follower_Response>> response)
//            {
//                if (response.isSuccessful() && response.body() != null)
//                {
//                    List<follower_Response> responseResult = response.body();
//                    for(int i=0; i<responseResult.size(); i++){;
//                        followerList.add(new follower_Response(responseResult.get(i).getFollower_id(),
//                                responseResult.get(i).getNick_name(),
//                                responseResult.get(i).getProfile_file_name()));
//                    }
//                    follower_adapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<follower_Response>> call, @NonNull Throwable t)
//            {
//                Log.e("에러", t.getMessage());
//            }
//        });
//    }
//
//
//


}