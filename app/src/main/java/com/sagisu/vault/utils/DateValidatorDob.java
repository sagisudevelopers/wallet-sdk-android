package com.sagisu.vault.utils;

/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Parcel;

import com.google.android.material.datepicker.CalendarConstraints.DateValidator;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * A {@link DateValidator} that only allows dates from a given point onward to be clicked.
 */
public class DateValidatorDob implements DateValidator {

    public DateValidatorDob() {
    }

   // private Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    public static final Creator<DateValidatorDob> CREATOR =
            new Creator<DateValidatorDob>() {
                @Override
                public DateValidatorDob createFromParcel(Parcel source) {
                    return new DateValidatorDob();
                }

                @Override
                public DateValidatorDob[] newArray(int size) {
                    return new DateValidatorDob[size];
                }
            };

    @Override
    public boolean isValid(long date) {
        Date parsedDate = new Date(date);
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.YEAR, -18);
        return parsedDate.before(c2.getTime());

       /* utc.setTimeInMillis(date);
        int dayOfWeek = utc.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY;*/
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateValidatorDob)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        Object[] hashedFields = {};
        return Arrays.hashCode(hashedFields);
    }
}
