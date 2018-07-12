#!/bin/bash

set -e # exit with nonzero exit code if anything fails

if [[ $TRAVIS_BRANCH == "master" && $TRAVIS_PULL_REQUEST == "false" ]]; then

echo "Starting to update gh-pages\n"

#copy data we're interested in to other place
cp -R docs $HOME/docs

#go to home and setup git
cd $HOME
git config --global user.email "sungwook0115.kim@gmail.com"
git config --global user.name "RoySRose"

#using token clone gh-pages branch
git clone --quiet --branch=gh-pages https://ZBrxvfeGzS5jiUsUJ16QBu1PdO5WOf1seGQXdDPzOQBtR9KhoJoRX0PNq+wiSsg/9quJXGQ4+zTLUnpZB+v3jgcnSG8Dhvc+Vu1soNHjFcs+sgmL18nnKOe/sPjucBA5ViNxRqYH1mVyIwwqYJXdy8I6BbsbgM7JT6JspD4Q8qr7deHQ2QQCB7h1OqnzjBfwpTxYNw2sbbYVCBpSsgUxGJkOJRqJU84pAJ+MMSke3lVG46ptlJlfEP5930Z+n4oEJGIx0Yo7L1AIovJyerneQqxgSIAYm7A8os6wU38BcgxA5BP5qGins0cZ6TCSETBnyuQbgP3Lm6Kb2Uudezi01/aegd+x4Hl2n0S37pgKk32a3WLqLJMwZWEtGRiF6HNdwxBNuNsA1qYG5aF2utiCPsgVcwJJrMKXa/lZuvwMmWKogc6GNrhOCA8B8ck1VafYhbhJkWaERYXNMapvMbUWqnklygE/dbQxpgzbxwm9UWuFuqBhbn6P5jB6vracbXHmotTvtnB1kbmzH7FfqQ6WQi1nRR3GvAq0U7l+26FozprjsdmDAWV6I8/LTpoI9LsjjKV+E6sm+CHqKnIh/594H/T/gkbBpggpdqgO6i2yfrsHuvjsybFWFX6NZ71+w45LD0oP5adQSPBXGICOcIID83Bor3Vsr47rE7zL6BH0t0M=@github.com/RoySRose/pinpoint.git gh-pages > /dev/null

#go into directory and copy data we're interested in to that directory
cd gh-pages
cp -Rf $HOME/docs/* ./pages

echo "Allow files with underscore https://help.github.com/articles/files-that-start-with-an-underscore-are-missing/" > .nojekyll
echo "[View live](https://${GH_USER}.github.io/${GH_REPO}/)" > README.md

#add, commit and push files
git add -f .
git commit -m "Auto build with Travis $TRAVIS_BUILD_NUMBER"
git push -fq origin gh-pages > /dev/null

echo "Done updating gh-pages\n"

else
 echo "Skipped updating gh-pages, because build is not triggered from the master branch."
fi;