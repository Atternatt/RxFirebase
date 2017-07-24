package com.m2f.rxfirebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by marc on 24/7/17.
 */
@RunWith(MockitoJUnitRunner::class)
class DatabaseTest {

    @Mock private lateinit var mockReference: DatabaseReference

    @Mock private lateinit var mockDataSnapshot: DataSnapshot

    private val testData = TestData()

    private val testDataList: MutableList<TestData> = mutableListOf()

    private val testDataMap: MutableMap<String, TestData> = mutableMapOf()


    @Test fun observeSingleValueReturnJustOneValue() {

    }

    @Test fun observeSingleValueWithoutData() {

    }

    @Test fun observeSingleValueWithWrongtype() {

    }

    @Test fun observeSingleValue_Disconnected() {

    }

    @Test fun observeSingleValue_Failed() {

    }

    @Test fun testSingleValueEvent() {

    }

    @Test fun testObserveValueEventList() {

    }

    @Test fun testObserveValuesMap() {

    }

    @Test fun testObserveChildEvent_Added() {

    }

    @Test fun testObserveChildEvent_Changed() {

    }

    @Test fun testObserveChildEvent_Removed() {

    }

    @Test fun testObserveChildEvent_Moved() {

    }

    @Test fun testObserveChildEvent_Cancelled() {

    }
}