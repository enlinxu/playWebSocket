package main

import (
	"crypto/tls"
	"encoding/base64"
	"fmt"
	"github.com/golang/glog"
	"github.com/gorilla/websocket"
	"net/http"
	"net/url"
	"time"
)

const (
	// Time allowed to write a message to the peer.
	writeWait = 10 * time.Second
)

type ConnectionConfig struct {
	url              string
	user             string
	password         string
	scheme           string
	handshakeTimeout time.Duration
}

func NewConnectionConfig(host, path, user, passwd string) (*ConnectionConfig, error) {
	addr, err := url.Parse(host)
	if err != nil {
		glog.Errorf("Failed to parse host(%v): %v", host, err)
		return nil, err
	}

	scheme := "ws"
	if addr.Scheme == "https" {
		scheme = "wss"
	}

	u := url.URL{
		Scheme: scheme,
		Host:   addr.Host,
		Path:   path,
	}

	return &ConnectionConfig{
		url:              u.String(),
		user:             user,
		password:         passwd,
		scheme:           scheme,
		handshakeTimeout: time.Second * 60,
	}, nil
}

func buildAuthHeader(conf *ConnectionConfig) http.Header {
	if len(conf.user) < 0 || len(conf.password) < 0 {
		glog.Warning("user name or password is empty")
		return nil
	}

	dat := []byte(fmt.Sprintf("%s:%s", conf.user, conf.password))
	header := http.Header{
		"Authorization": {"Basic " + base64.StdEncoding.EncodeToString(dat)},
	}

	return header
}

func getWebSocketConn(conf *ConnectionConfig) (*websocket.Conn, error) {
	fmt.Println("connection to: " + conf.url)

	//1. connection dialer
	d := &websocket.Dialer{
		HandshakeTimeout: conf.handshakeTimeout,
	}
	if conf.scheme == "wss" {
		d.TLSClientConfig = &tls.Config{InsecureSkipVerify: true}
	}

	//2. header
	header := buildAuthHeader(conf)

	//3. connect it
	wsocket, _, err := d.Dial(conf.url, header)
	if err != nil {
		glog.Errorf("Failed to connect to server(%s): %v", conf.url, err)
		return nil, err
	}

	h := func(message string) error {
		glog.V(3).Infof("Recevied ping msg")
		wsocket.WriteControl(websocket.PongMessage, []byte(message), time.Now().Add(writeWait))
		return nil
	}
	wsocket.SetPingHandler(h)

	return wsocket, nil
}

func testEcho(conf *ConnectionConfig) {
	wsocket, err := getWebSocketConn(conf)
	if err != nil {
		glog.Errorf("Failed to build websocket connection: %v", err)
		return
	}
	defer wsocket.Close()

	msgOut := []byte("hello world")
	//wsocket.WriteMessage(websocket.TextMessage, msgOut)
	wsocket.WriteMessage(websocket.BinaryMessage, msgOut)

	msgtype, msgIn, err := wsocket.ReadMessage()
	if err != nil {
		glog.Errorf("Failed to receive msg: %v", err)
	}
	fmt.Printf("Received msg type(%d): %v\n", msgtype, string(msgIn))

	//time.Sleep(time.Second * 12)
	wsocket.WriteMessage(websocket.CloseMessage, []byte(""))
}
