package ir.mjahanbazi.databaseusingroom;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MJTimeViewModel extends AndroidViewModel {
    private MJTimeRepository repository;
    private final LiveData<List<MJTime>> allTime;

    public MJTimeViewModel(Application application) {
        super(application);
        repository = new MJTimeRepository(application);
        allTime = repository.getAllTime();
    }

    public LiveData<List<MJTime>> getAllTime() {
        return allTime;
    }

    public void insert(MJTime currentTime) {
        repository.insert(currentTime);
    }

    public void update(long id, String currentTime) {
        repository.update(id, currentTime);
    }

    public void delete(MJTime currentTime) {
        repository.delete(currentTime);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
