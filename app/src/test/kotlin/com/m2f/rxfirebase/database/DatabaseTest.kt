package com.m2f.rxfirebase.database

import com.google.firebase.database.*
import com.m2f.rxfirebase.TestData
import com.m2f.rxfirebase.WrongType
import io.kotlintest.mock.mock
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by marc on 24/7/17.
 */
@RunWith(MockitoJUnitRunner::class)
class DatabaseTest {

    @Mock private lateinit var mockDatabase: DatabaseReference

    @Mock private lateinit var mockDataSnapshot: DataSnapshot

    private val testData = TestData()

    private val testDataList: List<TestData> = listOf(testData)

    private val testDataMap: Map<String, TestData> = mapOf("key" to testData)

    @Before fun setup() {

        `when`(mockDataSnapshot.exists()).thenReturn(true)
        `when`(mockDataSnapshot.getValue(TestData::class.java)).thenReturn(testData)
        //`when`(mockDataSnapshot.key).thenReturn("key")
        //`when`(mockDataSnapshot.children).thenReturn(listOf(mockDataSnapshot))
    }


    @Test fun `observe single value return a single object and completes`() {


        val subs: TestObserver<TestData> = TestObserver()
        mockDatabase
                .observeSingleValue<TestData>()
                .subscribe(subs)

        val argument: ArgumentCaptor<ValueEventListener> = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture())
        argument.value.onDataChange(mockDataSnapshot)

        subs.assertNoErrors()
                .assertValueCount(1)
                .assertComplete()
                .assertResult(testData)
        subs.dispose()


    }

    @Test fun `observe value without data completes emitting nothing`() {

        val noData: DataSnapshot = mock()
        `when`(noData.exists()).thenReturn(false)

        val subs: TestObserver<TestData> = TestObserver()
        mockDatabase
                .observeSingleValue<TestData>()
                .subscribe(subs)

        val argument: ArgumentCaptor<ValueEventListener> = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture())
        argument.value.onDataChange(noData)

        subs.assertNoErrors()
                .assertNoValues()
                .assertComplete()
        subs.dispose()


    }

    @Test fun `observe single value with wrong data emits a cast exception`() {

        val subs: TestObserver<WrongType> = TestObserver()
        mockDatabase
                .observeSingleValue<WrongType>()
                .subscribe(subs)

        val argument: ArgumentCaptor<ValueEventListener> = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture())
        argument.value.onDataChange(mockDataSnapshot)

        subs.assertError(ClassCastException::class.java)
                .assertNoValues()
                .assertNotComplete()
        subs.dispose()

    }

    @Test fun `observe single value when database is disconnected emits a DatabaseException without completing`() {
        val subs: TestObserver<TestData> = TestObserver()
        mockDatabase
                .observeSingleValue<TestData>()
                .subscribe(subs)

        val argument = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture())
        argument.value.onCancelled(DatabaseError.zzbU(DatabaseError.DISCONNECTED))

        subs.assertError(DatabaseException::class.java)
                .assertNoValues()
                .assertNotComplete()
        subs.dispose()
    }

    @Test fun `observe single value when database failed emits a DatabaseException without completing`() {
        val subs: TestObserver<TestData> = TestObserver()
        mockDatabase
                .observeSingleValue<TestData>()
                .subscribe(subs)

        val argument = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture())
        argument.value.onCancelled(DatabaseError.zzbU(DatabaseError.OPERATION_FAILED))

        subs.assertError(DatabaseException::class.java)
                .assertNoValues()
                .assertNotComplete()
        subs.dispose()
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