#!/bin/sh
export FABRIC_CFG_PATH=${PWD}

rm -fr config/*
rm -fr crypto-config/*

mkdir -p config
mkdir -p crypto-config

cryptogen generate --config=./crypto-config.yaml

configtxgen -profile OneOrgOrdererGenesis -outputBlock ./config/genesis.block

configtxgen -profile OneOrgChannel -outputCreateChannelTx ./config/channel.tx -channelID mychannel

configtxgen -profile OneOrgChannel -outputAnchorPeersUpdate ./config/Org1MSPanchors.tx -channelID mychannel -asOrg Org1MSP

