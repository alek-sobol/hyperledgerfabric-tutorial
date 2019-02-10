package tutorial.chaincode;


import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CarMileageChaincodeTest {

    @Mock
    private ChaincodeStub chaincodeStub;
    private CarMileageChaincode carMileageChaincode = new CarMileageChaincode();

    @Test
    public void shouldReturnErrorForIncorrectFunctionName() {
        //given
        String functionName = "wrong_name";
        given(chaincodeStub.getFunction()).willReturn(functionName);

        //when
        Chaincode.Response result = carMileageChaincode.invoke(chaincodeStub);

        //then
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo("wrong_name function is currently not supported");
    }

    @Test
    public void shouldReturnErrorForToMuchArgumentsNumberOfInvoke() {
        //given
        String functionName = "invoke";
        given(chaincodeStub.getFunction()).willReturn(functionName);
        given(chaincodeStub.getParameters()).willReturn(Arrays.asList("car_id", "3245000", "456"));

        //when
        Chaincode.Response result = carMileageChaincode.invoke(chaincodeStub);

        //then
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo("incorrect number of arguments");
    }

    @Test
    public void shouldReturnErrorForToLittleArgumentsNumberOfInvoke() {
        //given
        String functionName = "invoke";
        given(chaincodeStub.getFunction()).willReturn(functionName);
        given(chaincodeStub.getParameters()).willReturn(Collections.singletonList("car_id"));

        //when
        Chaincode.Response result = carMileageChaincode.invoke(chaincodeStub);

        //then
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo("incorrect number of arguments");
    }

    @Test
    public void shouldReturnErrorForToMuchArgumentsNumberOfQuery() {
        //given
        String functionName = "query";
        given(chaincodeStub.getFunction()).willReturn(functionName);
        given(chaincodeStub.getParameters()).willReturn(Arrays.asList("car_id", "565657"));

        //when
        Chaincode.Response result = carMileageChaincode.invoke(chaincodeStub);

        //then
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo("incorrect number of arguments");
    }

    @Test
    public void shouldReturnErrorForToLittleArgumentsNumberOfQuery() {
        //given
        String functionName = "query";
        given(chaincodeStub.getFunction()).willReturn(functionName);
        given(chaincodeStub.getParameters()).willReturn(Collections.emptyList());

        //when
        Chaincode.Response result = carMileageChaincode.invoke(chaincodeStub);

        //then
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo("incorrect number of arguments");
    }

    @Test
    public void shouldReturnErrorForPassingIncorrectMileage() {
        //given
        String functionName = "invoke";
        String carId = "id-1246789";
        String currentMileageAsString = "1300";
        String mileageToUpdate = "300";

        given(chaincodeStub.getFunction()).willReturn(functionName);
        given(chaincodeStub.getParameters()).willReturn(Arrays.asList(carId, mileageToUpdate));

        given(chaincodeStub.getStringState(carId)).willReturn(currentMileageAsString);

        //when
        Chaincode.Response result = carMileageChaincode.invoke(chaincodeStub);

        //then
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo("incorrect value");
    }

    @Test
    public void shouldReturnSuccessForNewState() {
        //given
        String functionName = "invoke";
        String carId = "id-1246789";
        String mileageToUpdate = "300";

        given(chaincodeStub.getFunction()).willReturn(functionName);
        given(chaincodeStub.getParameters()).willReturn(Arrays.asList(carId, mileageToUpdate));

        //when
        Chaincode.Response result = carMileageChaincode.invoke(chaincodeStub);

        //then
        assertThat(result.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void shouldSuccessfullyUpdateMileage() {
        //given
        given(chaincodeStub.getFunction()).willReturn("invoke");
        given(chaincodeStub.getParameters()).willReturn(Arrays.asList("id-1246789", "300"));
        given(chaincodeStub.getStringState("id-1246789")).willReturn("100");

        //when
        Chaincode.Response result = carMileageChaincode.invoke(chaincodeStub);

        //then
        assertThat(result.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void shouldSuccessfullyReturnCorrectState() {
        //given
        String functionName = "query";
        String carId = "id-1246789";
        String currentMileageAsString = "100";

        given(chaincodeStub.getFunction()).willReturn(functionName);
        given(chaincodeStub.getParameters()).willReturn(Collections.singletonList(carId));
        given(chaincodeStub.getStringState(carId)).willReturn(currentMileageAsString);

        //when
        Chaincode.Response result = carMileageChaincode.invoke(chaincodeStub);

        //then
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo(currentMileageAsString);
    }

    @Test
    public void shouldReturnJsonObjectForHistoryData() {
        //given
        String functionName = "queryHistory";
        String carId = "id-1246789";
        given(chaincodeStub.getFunction()).willReturn(functionName);
        given(chaincodeStub.getParameters()).willReturn(Collections.singletonList(carId));
        Instant instant1 = Instant.ofEpochMilli(1543005164982L);
        Instant instant2 = Instant.ofEpochMilli(1549805164982L);

        KeyModification firstKeyModification = new TestKeyModification("tx345tgd564d", "130867", instant1, false);
        KeyModification secondKeyModification = new TestKeyModification("txhjo0095wr", "164567", instant2, false);
        QueryResultsIterator<KeyModification> queryResultsIterator = mockQueryResultIterator(firstKeyModification, secondKeyModification);
        when(chaincodeStub.getHistoryForKey(carId)).thenReturn(queryResultsIterator);

        //when
        Chaincode.Response result = carMileageChaincode.invoke(chaincodeStub);

        //then
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("{\"transactions\":[[{\"transactionId\":\"tx345tgd564d\",\"timestamp\":\""+instant1.toString()+"\",\"value\":\"130867\",\"isDeleted\":false},{\"transactionId\":\"txhjo0095wr\",\"timestamp\":\""+instant2.toString()+"\",\"value\":\"164567\",\"isDeleted\":false}]]}");
    }

    @SuppressWarnings("unchecked")
    private QueryResultsIterator<KeyModification> mockQueryResultIterator(KeyModification firstKey, KeyModification seconddKey) {
        QueryResultsIterator<KeyModification> queryResultsIterator = mock(QueryResultsIterator.class);
        Iterator<KeyModification> iterator = mock(Iterator.class);
        when(queryResultsIterator.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(firstKey, seconddKey);
        doCallRealMethod().when(queryResultsIterator).forEach(any());
        return queryResultsIterator;
    }

    private class TestKeyModification implements KeyModification {

        private String txId;
        private String value;
        private Instant timestamp;
        private boolean deleted;

        TestKeyModification(String txId, String value, Instant timestamp, boolean deleted) {
            this.txId = txId;
            this.value = value;
            this.timestamp = timestamp;
            this.deleted = deleted;
        }

        @Override
        public String getTxId() {
            return txId;
        }

        @Override
        public byte[] getValue() {
            return new byte[0];
        }

        @Override
        public String getStringValue() {
            return value;
        }

        @Override
        public Instant getTimestamp() {
            return timestamp;
        }

        @Override
        public boolean isDeleted() {
            return deleted;
        }
    }
}