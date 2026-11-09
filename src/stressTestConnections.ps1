for ($i = 1; $i -le 50; $i++){
    wt -w 0 new-tab powershell -NoExit -Command "telnet localhost 5000"
}