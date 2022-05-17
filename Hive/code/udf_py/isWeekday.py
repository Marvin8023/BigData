from datetime import date, datetime
import sys
import time

def if_weekday(ts):
    timeArray = time.localtime(int(ts))
    timeFormat = time.strftime("%Y--%M---%D",timeArray)
    day = datetime.strptime(timeFormat, "%Y--%M--%D")

    if day.weekday() in [5, 6]:
        return "1"
    else:
        return "0"

for line in sys.stdin:
    value = line.strip().split("\t")
    uid = value[0]
    iid = value[1]
    score = value[2]
    times = value[3]
    print("\t".join([uid,iid,score,times,if_weekday(times)]))