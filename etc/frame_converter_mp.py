import sys, cv2
from multiprocessing import Pool, cpu_count, Lock
from PIL import Image

filename = sys.argv[1]
outputname = sys.argv[2]
width = int(sys.argv[3])
height = int(sys.argv[4])
keying_freq = int(sys.argv[5])
mp_count = cpu_count() # TODO insert cpu count as param(maybe)

lock = Lock()
video = cv2.VideoCapture(filename)
fps = video.get(cv2.CAP_PROP_FPS)
frame_count = int(video.get(cv2.CAP_PROP_FRAME_COUNT))
duration = (frame_count / fps) * 1000 / keying_freq  # video duration in milliseconds


def algorithm(idx):
    curr_step = int(duration / mp_count) * idx  # starting index
    end = int(duration / mp_count) * (idx + 1)  # ending index
    with open(f"{idx}_{outputname}", "w") as file:
        success, image = video.read()
        while success:
            try:
                with lock:  # lock a video to extract frame
                    video.set(cv2.CAP_PROP_POS_MSEC, curr_step * keying_freq)
                    success, image = video.read()
                    img = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
            except:
                success = False
            if not success:
                break
            frame = Image.fromarray(img)
            frame = frame.resize((width, height))
            for i in range(frame.width):
                for j in range(frame.height):
                    pos = i, j
                    px = frame.getpixel(pos)
                    r = int(px[0])
                    g = int(px[1])
                    b = int(px[2])
                    file.write(f"{r: <3} {g: <3} {b: <3} \t")
                file.write("\n")
            file.write('End of frame no. ' + str(curr_step) + '\n')
            curr_step += 1
            if curr_step == end and not idx == mp_count - 1:
                break


if __name__ == "__main__":
    pool = Pool(mp_count)
    pool.map(algorithm, range(mp_count))
