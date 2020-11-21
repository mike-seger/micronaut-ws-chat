var ws;
var logContainer;
var historyContainer;
var messagesHistory = [];

if( typeof Element.prototype.clearChildren === 'undefined' ) {
    Object.defineProperty(Element.prototype, 'clearChildren', {
      configurable: true,
      enumerable: false,
      value: function() {
        while(this.firstChild) this.removeChild(this.lastChild);
      }
    });
}

function connectToServer() {
    var icon = document.getElementById("connection_error_icon");

    try {
        ws = new WebSocket(document.getElementById("serverSelected").value);
        document.getElementById("connectionStatus").innerHTML = "Connecting";

        ws.onopen = function () {
            document.getElementById("connectionStatus").innerHTML = "Connected";
        };
        ws.onmessage = function (event) {
            createLogLine(JSON.parse(event.data));
        };
        ws.onclose = function () {
            document.getElementById("connectionStatus").innerHTML = "Disconnected";
        };
        icon.style.visibility = "hidden";
        icon.title = '';
    } catch (e) {
        icon.style.visibility = "visible";
        icon.title = e.message;
    }
}

function storeHistory() {
    if (messagesHistory && messagesHistory.length >= 100) {
        messagesHistory = messagesHistory.slice(0, 100)
    }
    localStorage.setItem("history", JSON.stringify(messagesHistory));
}

function getHistory() {
    var his = localStorage.getItem("history");
    if (his) {
        try {
            his = JSON.parse(his);
            messagesHistory = his;
        } catch (exc) {
        }
    }
}

function clearHistory() {
    messagesHistory = [];
    localStorage.removeItem("history");
    historyContainer.clearChildren();
}

function submitOnEnter(event){
    if(event && event.which === 13 && !event.shiftKey) {
        sendMessage();
        event.preventDefault(); // Prevents the addition of a new line in the text field (not needed in a lot of cases)
    }
}

function clearLog() {
    if (!logContainer) {
        logContainer = document.getElementById("responseFromServer");
    }
    while (logContainer.firstChild) {
        logContainer.removeChild(logContainer.firstChild);
    }
}

function createLogLine(msg) {
    var logLine = document.createElement("li");

    var timeStamp = new Date(msg.timeStamp);
    var html = new Date(timeStamp.getTime() -
         (timeStamp.getTimezoneOffset()*60000)).toISOString().replace("T", " ").substring(0, 19)
         + ": " + msg.userName + "\n";

    if(msg.contentType === "markdown") {
        var converter = new showdown.Converter(),
        html = html + converter.makeHtml(msg.content);
    } else {
        html = html + msg.content;
        logLine.className = 'list-group-item-text';
    }

    logLine.innerHTML = html;
    appendLogLine(logLine);
}

function appendLogLine(line) {
    if (!logContainer) {
        logContainer = document.getElementById("responseFromServer");
    }
    if (logContainer.hasChildNodes()) {
        logContainer.insertBefore(line, logContainer.firstChild);
    } else {
        logContainer.appendChild(line);
    }
}

function createHistoryLine(msg) {
    if (!msg.date) {
        msg.date = new Date();
    }
    else if (typeof msg.date === "string") {
        msg.date = new Date(msg.date);
    }
    var logLine = document.createElement("li");
    var data = "<div>URL: <span>" + msg.url + "</span></div>\
            <div>message: <span>" + msg.message + "</span></div>\
            "
            ;
    logLine.innerHTML = data;
    logLine.onclick = function () {
        historySelect(msg);
    };
    logLine.className = 'list-group-item';
    appendHistoryLine(logLine);
}

function appendHistoryLine(line) {
    if (!historyContainer) {
        historyContainer = document.getElementById("historyContainer");
    }
    if (historyContainer.hasChildNodes()) {
        historyContainer.insertBefore(line, historyContainer.firstChild);
    } else {
        historyContainer.appendChild(line);
    }
}

function getInTwoDigits(num) {
    return num < 10 ? '0' + num : '' + num;
}

function disconnectFromServer() {
    ws.close();
}

function sendMessage() {
    var icon = document.getElementById("error_icon");
    try {
        var msgObj = document.getElementById("msgToServer").value;
        var contentType = document.getElementById("contentType");
        var message = {
            content : msgObj,
            contentType : contentType.options[contentType.selectedIndex].value
        }
        var messageJson = JSON.stringify(message);
        ws.send(messageJson);
        icon.style.visibility = "hidden";
        icon.title = '';
        var historyLine = {
            url: document.getElementById("serverSelected").value,
            message: messageJson,
            date: new Date()
        };
        messagesHistory.unshift(historyLine);
        createHistoryLine(historyLine);
        storeHistory();
        document.getElementById("msgToServer").value = ""
    } catch (e) {
        icon.style.visibility = "visible";
        icon.title = e.message;
    }
}

function historySelect(msg) {
    document.getElementById("serverSelected").value = msg.url;
    message = JSON.parse(msg.message);
    document.getElementById("msgToServer").value = message.content;
    document.getElementById("contentType").value = message.contentType;
}

function loadHistory() {
    getHistory();
    if (messagesHistory && messagesHistory.length) {
        for (var i = messagesHistory.length - 1; i >= 0; i--) {
            createHistoryLine(messagesHistory[i]);
        }
    }
}
