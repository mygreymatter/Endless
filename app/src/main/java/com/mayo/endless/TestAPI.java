package com.mayo.endless;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Mahayogi Lakshmipathi on 11/12/16.
 *
 * @author <a href="mailto:mygreymatter@gmail.com">Mahayogi Lakshmipathi</a>
 * @version 1.0
 */

public interface TestAPI {
    @GET("test_list/{page}")
    Call<ArrayList<Test>> getTests(@Path("page") String page);
}
