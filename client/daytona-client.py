import json
import urllib2

def sendMessage( map, url ): 
	msg = json.dumps( map )
	opener = urllib2.build_opener( urllib2.HTTPHandler )
	request = urllib2.Request( url, data=msg )
	request.get_method = lambda: 'PUT'
	urlobj = opener.open( request )


if __name__ == "__main__":
	sendMessage( { 'sender' : 'make-o-matic', 'text' : 'Hello from Python' }, 'http://localhost:8080/daytona/notify?format=json' )
