package com.example.tmdb.dataClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * These two classes are responsible for representing the foreign key table linking between credits
 * and movie/series. (We need these tables as the relation between credits and multimedia
 * is many to many)
 */
@Entity(
    primaryKeys = ["media", "credits"], foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["name"],
            childColumns = ["credits"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Movie::class,
            parentColumns = ["id"],
            childColumns = ["media"],
            onDelete = ForeignKey.CASCADE
        )])
class CreditsAndMoviesForeignKeyTable(@ColumnInfo(index = true) var media: Int, @ColumnInfo(index = true) var credits: String)


@Entity(
    primaryKeys = ["media", "credits"], foreignKeys = [
        ForeignKey(
            entity = Series::class,
            parentColumns = ["id"],
            childColumns = ["media"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Person::class,
            parentColumns = ["name"],
            childColumns = ["credits"],
            onDelete = ForeignKey.CASCADE
        )]
)
class CreditsAndSeriesForeignKeyTable(@ColumnInfo(index = true)  var media: Int, @ColumnInfo(index = true)  var credits: String)