package com.example.flowledger.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.flowledger.data.db.entity.TransactionWithCategory;
import com.example.flowledger.data.repository.TransactionRepository;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private final TransactionRepository repository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<TransactionWithCategory>> searchResults;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        searchResults = Transformations.switchMap(searchQuery, repository::searchTransactions);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<List<TransactionWithCategory>> getSearchResults() {
        return searchResults;
    }
}
