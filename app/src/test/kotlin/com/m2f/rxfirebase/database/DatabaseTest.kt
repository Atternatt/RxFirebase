package com.m2f.rxfirebase.database

import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.m2f.rxfirebase.TestData
import com.m2f.rxfirebase.WrongType
import exceptions.ElementNotExistsException
import io.kotlintest.mock.mock
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber
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

    @Mock private lateinit var mockDocumentReference: DocumentReference

    @Mock private lateinit var mockDataSnapshot: DataSnapshot

    private val testData = TestData()

    @Before fun setup() {

        `when`(mockDataSnapshot.exists()).thenReturn(true)
        `when`(mockDataSnapshot.getValue(TestData::class.java)).thenReturn(testData)
        `when`(mockDataSnapshot.key).thenReturn("key")
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

    @Test fun `observe single value without data completes emitting nothing`() {

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
        argument.value.onCancelled(DatabaseError.zzfx(DatabaseError.DISCONNECTED))

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
        argument.value.onCancelled(DatabaseError.zzfx(DatabaseError.OPERATION_FAILED))

        subs.assertError(DatabaseException::class.java)
                .assertNoValues()
                .assertNotComplete()
        subs.dispose()
    }

    @Test fun `observe value return at least one object and doesn't complete`() {
        //with
        val subs = TestSubscriber<TestData>()
        mockDatabase
                .observeValueEvent<TestData>()
                .subscribe(subs)

        //when
        val argument: ArgumentCaptor<ValueEventListener> = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockDatabase).addValueEventListener(argument.capture())
        argument.value.onDataChange(mockDataSnapshot)

        //then
        subs.assertNoErrors()
                .assertNotComplete()
                .assertValueCount(1)
                .assertValue { it.value == testData.value }
        subs.dispose()

    }

    @Test fun `observe value without data emitts a ElementNotExistsException without completing`() {

        val noData: DataSnapshot = mock()
        `when`(noData.exists()).thenReturn(false)

        val subs = TestSubscriber<TestData>()
        mockDatabase
                .observeValueEvent<TestData>()
                .subscribe(subs)

        val argument: ArgumentCaptor<ValueEventListener> = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockDatabase).addValueEventListener(argument.capture())
        argument.value.onDataChange(noData)

        subs.assertError(ElementNotExistsException::class.java)
                .assertNoValues()
                .assertNotComplete()
        subs.dispose()
    }

    @Test fun `observe child event emits a ChildEvent with ADDED EventType when added something`() {
        //with
        val subs = TestSubscriber<ChildEvent<TestData>>()
        mockDatabase
                .observeChildEvents<TestData>()
                .subscribe(subs)

        //when
        val argument: ArgumentCaptor<ChildEventListener> = ArgumentCaptor.forClass(ChildEventListener::class.java)
        verify(mockDatabase).addChildEventListener(argument.capture())
        argument.value.onChildAdded(mockDataSnapshot, "root")

        //then
        subs.assertNoErrors()
                .assertNotComplete()
                .assertValueCount(1)
                .assertValue { it.eventType == ChildEvent.EventType.ADDED }
        subs.dispose()
    }

    @Test fun `observe child event emits a ChildEvent with CHANGED EventType when changed something`() {
        //with
        val subs = TestSubscriber<ChildEvent<TestData>>()
        mockDatabase
                .observeChildEvents<TestData>()
                .subscribe(subs)

        //when
        val argument: ArgumentCaptor<ChildEventListener> = ArgumentCaptor.forClass(ChildEventListener::class.java)
        verify(mockDatabase).addChildEventListener(argument.capture())
        argument.value.onChildChanged(mockDataSnapshot, "root")

        //then
        subs.assertNoErrors()
                .assertNotComplete()
                .assertValueCount(1)
                .assertValue { it.eventType == ChildEvent.EventType.CHANGED }
        subs.dispose()

    }

    @Test fun `observe child event emits a ChildEvent with REMOVED EventType when removed something`() {
        //with
        val subs = TestSubscriber<ChildEvent<TestData>>()
        mockDatabase
                .observeChildEvents<TestData>()
                .subscribe(subs)

        //when
        val argument: ArgumentCaptor<ChildEventListener> = ArgumentCaptor.forClass(ChildEventListener::class.java)
        verify(mockDatabase).addChildEventListener(argument.capture())
        argument.value.onChildRemoved(mockDataSnapshot)

        //then
        subs.assertNoErrors()
                .assertNotComplete()
                .assertValueCount(1)
                .assertValue { it.eventType == ChildEvent.EventType.REMOVED }
        subs.dispose()
    }

    @Test fun `observe child event emits a ChildEvent with MOVED EventType when moved something`() {
        //with
        val subs = TestSubscriber<ChildEvent<TestData>>()
        mockDatabase
                .observeChildEvents<TestData>()
                .subscribe(subs)

        //when
        val argument: ArgumentCaptor<ChildEventListener> = ArgumentCaptor.forClass(ChildEventListener::class.java)
        verify(mockDatabase).addChildEventListener(argument.capture())
        argument.value.onChildMoved(mockDataSnapshot, "root")

        //then
        subs.assertNoErrors()
                .assertNotComplete()
                .assertValueCount(1)
                .assertValue { it.eventType == ChildEvent.EventType.MOVED }
        subs.dispose()
    }

    @Test fun `observe for child events receive a DatabaseException if query is cancelled`() {
        //with
        val subs = TestSubscriber<ChildEvent<TestData>>()
        mockDatabase
                .observeChildEvents<TestData>()
                .subscribe(subs)

        //when
        val argument: ArgumentCaptor<ChildEventListener> = ArgumentCaptor.forClass(ChildEventListener::class.java)
        verify(mockDatabase).addChildEventListener(argument.capture())
        argument.value.onCancelled(DatabaseError.zzfx(DatabaseError.DISCONNECTED))

        //then
        subs.assertError(DatabaseException::class.java)
                .assertNoValues()
                .assertNotComplete()
        subs.dispose()
    }

    @Test fun `observe child events without data emitts a ElementNotExistsException without completing`() {

        val noData: DataSnapshot = mock()
        `when`(noData.exists()).thenReturn(false)

        val subs = TestSubscriber<ChildEvent<TestData>>()
        mockDatabase
                .observeChildEvents<TestData>()
                .subscribe(subs)

        val argument: ArgumentCaptor<ChildEventListener> = ArgumentCaptor.forClass(ChildEventListener::class.java)
        verify(mockDatabase).addChildEventListener(argument.capture())
        argument.value.onChildAdded(noData, "root")

        subs.assertError(ElementNotExistsException::class.java)
                .assertNoValues()
                .assertNotComplete()
        subs.dispose()
    }
}