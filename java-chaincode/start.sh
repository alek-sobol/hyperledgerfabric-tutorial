docker-compose -f docker-compose.yaml down
docker-compose -f docker-compose.yaml up -d
sleep 10
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.mydomain.com/msp" peer0.org1.mydomain.com peer channel create -o orderer.mydomain.com:7050 -c mychannel -f /etc/hyperledger/configtx/channel.tx
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.mydomain.com/msp" peer0.org1.mydomain.com peer channel join -b mychannel.block






