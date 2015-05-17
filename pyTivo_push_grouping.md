A simple way to group a bunch of titles together on TiVo Now Playing List with pyTivo pushes (pyTivo pushes group differently than pyTivo pulls):

1. Put all videos you want grouped together on TiVo in a single folder.

2. In that folder put a file named default.txt with a unique seriesId tag (doesn't matter what it is, just don't repeat it for other folder groupings):
```
seriesId : some_unique_folder_name
```

3. For each video in that folder, make an accompanying metadata file:
For example, for the video video1.mpg, there would be a metadata file named video1.mpg.txt with the title name you want to have on TiVo:
```
title : title1
```

(You can also add other tags such as **description** for example if you wish).

NOTE: The **title** of the first file that is pushed will control the folder name, so in above example folder name would become **title1**, and subsequent videos in same folder would group under the same **title1** folder.

# FULL EXAMPLE #
1. Under a folder I have a default.txt file with:
```
seriesId : Kids_Movies_folder
```

2. Under same folder I have movie1.mpg with movie1.mpg.txt:
```
title : Kids Movies
description: This is Kid Movie 1
```

3. Now I push movie1.mpg

4. Under same folder I now have movie2.mp4 with movie2.mp4.txt:
```
title : Kid Movie 2
description : This is Kid Movie 2
```

5. Now I push movie2.mp4

Result on TiVo:
```
Kids Movies (2)
   Kid Movie 2 (description=This is Kid Movie 2)
   Kids Movies (description=This is Kid Movie 1)
```

## METADATA TAGS SUPPORTED FOR PUSHES ##
```
seriesId (not used in same way as pulls but useful for grouping purposes)
title/seriesTitle
episodeTitle
description
```