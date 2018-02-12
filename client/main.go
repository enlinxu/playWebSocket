package main

import (
	"flag"
	"fmt"
)

var (
	server = "http://127.0.0.1:8080"
	path   = "/echo/websocket/echoAnnotation"
)

func setFlags() {
	flag.StringVar(&server, "serverHost", server, "websocket server address")
	flag.StringVar(&path, "path", path, "webosocket resource path")
	flag.Parse()
}

func main() {
	setFlags()

	conf, err := NewConnectionConfig(server, path, "", "")
	if err != nil {
		fmt.Errorf("Failed to build a configuration for websocket: %v", err)
	}

	testEcho(conf)
}
