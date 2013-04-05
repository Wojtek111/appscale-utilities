import threading
import sys
import os
import time

class ThreadClass(threading.Thread):
  #This function executes hawkeye tests until the time limit is met
  def run(self):
    os.system('mkdir ' + self.getName())
    now = int(round(time.time() * 1000))
    finalTime = now + numMins * 60 * 1000
    
    while True:
      self.executeHawkeyeTest()
      now = int(round(time.time() * 1000))
      if(now > finalTime):
        break;      

  #This function executes the hawkeye test in the threads own directory  
  def executeHawkeyeTest(self):
    os.system('cd ' + self.getName() + '; /usr/local/Python-2.7.3/python'\
                ' /root/hawkeye/test-suite/hawkeye.py -s ' + hawkeyeUrl + ' -l '\
                + hawkeyeLang + ' -p ' + hawkeyePort + ' --suites=' + hawkeyeSuites + ' >> '\
                + self.getName() + '.log')

#This is the beginning of the main code 
numThreads = int(sys.argv[1])
numMins = int(sys.argv[2])
hawkeyeUrl = sys.argv[3]
hawkeyePort = sys.argv[4]
hawkeyeLang = sys.argv[5]
hawkeyeSuites = sys.argv[6]

print 'Running load test with [' + str(numThreads) + '] threads, for [' + str(numMins) + ']'\
       ' minutes, hawkeyeUrl [' + hawkeyeUrl + '], hawkeyePort [' + hawkeyePort + '], language ['\
       + hawkeyeLang + '], suites [' + hawkeyeSuites + ']'

for i in range(numThreads):
  t = ThreadClass()
  t.start()
  
