package com.example.disciteomens;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DisciteOmnesApi {

    // Gruppen abrufen
    @GET("groups")
    Call<List<Group>> getGroups();

    // Neue Gruppe erstellen
    @POST("groups")
    Call<Group> createGroup(@Body Group request);

    // Gruppe aktualisieren
    @PUT("groups/{groupId}")
    Call<Group> updateGroup(@Path("groupId") String groupId, @Body Group group);

    // Alle tasks
    @GET("tasks")
    Call<List<Task>> getTasks(@Query("groupId") String groupId);
    @POST("tasks")
    Call<Task> addTask(@Body Task task);
    @PUT("tasks/{taskId}")
    Call<Task> updateTask(@Path("taskId") String taskId, @Body Task task);

    @DELETE("tasks/{taskId}")
    Call<Void> deleteTask(@Path("taskId") String taskId);
}
