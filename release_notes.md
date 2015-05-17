# RELEASE NOTES #

## v0.1d (04/23/2010) ##

**FIXES**
  * Fixed parsing problem for files with spaces when building list of already pushed files from .processed files. This problem would cause auto\_push to try and push files that already had been pushed previously when re-starting auto\_push.

## v0.1c (04/19/2010) ##

**FIXES**
  * List of already pushed titles is rebuilt every time auto push checks for files to push now. There was a bug in previous release where when first starting auto push that already pushed files were being pushed.

## v0.1b (02/14/2010) ##

**FIXES**
  * Updated pyTivo push call to work properly with change in pyTivo response to external push calls in recent releases. Prior to this fix auto\_push would always get 404 (Not Found) response from pyTivo server for push requests and so auto\_push would assume the push requests failed even though they would go through anyway.

## v0.1a (10/29/2009) ##

**Initial release**