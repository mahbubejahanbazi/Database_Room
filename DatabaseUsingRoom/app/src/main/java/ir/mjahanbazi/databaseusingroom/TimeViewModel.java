package ir.mjahanbazi.databaseusingroom;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TimeViewModel extends AndroidViewModel {
    private TimeRepository repository;
    private final LiveData<List<Time>> allTime;

    public TimeViewModel(Application application) {
        super(application);
        repository = new TimeRepository(application);
        allTime = repository.getAllTime();
    }

    public LiveData<List<Time>> getAllTime() {
        return allTime;
    }

    public void insert(Time currentTime) {
        repository.insert(currentTime);
    }

    public void update(long id, String currentTime) {
        repository.update(id, currentTime);
    }

    public void delete(Time currentTime) {
        repository.delete(currentTime);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
