package com.example.flowledger.data.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class TransactionWithCategory {
    @Embedded
    public Transaction transaction;

    @Relation(
            parentColumn = "categoryId",
            entityColumn = "id"
    )
    public Category category;
}
