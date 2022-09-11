package com.example.bubbly;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

public class Video_FullScreen extends AppCompatActivity {

    SimpleExoPlayerView vd_media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_full_screen);


        vd_media = findViewById(R.id.video_fullscreen_exoplayer);

        Bundle extras = getIntent().getExtras();
        String media_url = extras.getString("url");

        // bandwisthmeter : 기본 대역폭 가져오기
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // 기본 막대를 사용하는 동영상
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        // 트랙셀렉터 추가
        ExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        // url 로 부터 Uri 파싱
        Uri videouri = Uri.parse(media_url);
        // 엑소플레이어뷰
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
        // 미디어 소스 생성
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // 미디어 소스 생성
        MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
        // 엑소플레이어 넣기
        vd_media.setPlayer((SimpleExoPlayer) exoPlayer);
        vd_media.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        // 미리 준비
        exoPlayer.prepare(mediaSource);
        // 준비 완료시 재생 여부
        exoPlayer.setPlayWhenReady(false);
    }
}