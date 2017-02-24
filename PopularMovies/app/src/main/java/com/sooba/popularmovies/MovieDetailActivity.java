package com.sooba.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sooba.popularmovies.model.Movie;
import com.sooba.popularmovies.utilities.Constants;
import com.sooba.popularmovies.utilities.Utils;
import com.squareup.picasso.Picasso;

/**
 * Activity that shows a movie details
 */
public class MovieDetailActivity extends AppCompatActivity {

    // The movie to be detailed
    private Movie mMovie;

    // Views
    private TextView tvTitle;
    private ImageView ivPoster;
    private TextView tvOverview;
    private RatingBar rbMovieRating;
    private TextView tvReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Views initialization
        tvTitle = (TextView) findViewById(R.id.tv_detail_title);
        ivPoster = (ImageView) findViewById(R.id.iv_detail_poster);
        tvOverview = (TextView) findViewById(R.id.tv_detail_overview);
        rbMovieRating = (RatingBar) findViewById(R.id.rb_movie_rating);
        tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);

        Intent intent = getIntent();

        // Check if there is a received movie
        if(intent.hasExtra(Constants.MOVIE_EXTRA)) {

            // Gets the movie to be shown
            mMovie = (Movie) intent.getSerializableExtra(Constants.MOVIE_EXTRA);

            // Setup the views with the movie values
            tvTitle.setText(mMovie.getOriginalTitle());
            tvOverview.setText(mMovie.getOverview());
            rbMovieRating.setRating((float) mMovie.getVoteAverage());
            tvReleaseDate.setText(String.format(getString(R.string.release_date), mMovie.getReleaseDate()));

            String posterUrl = Utils.getPosterWidthByDpi(this) + mMovie.getPosterPath();
            Picasso.with(this).load(posterUrl).into(ivPoster);
        }
    }
}
