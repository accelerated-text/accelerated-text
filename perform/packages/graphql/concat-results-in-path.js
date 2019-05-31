import { assocPath, path }  from 'ramda';


export default resultPath => ( prevResult, { fetchMoreResult }) => (
    fetchMoreResult
        ? assocPath(
            resultPath,
            [
                ...path( resultPath, prevResult ),
                ...path( resultPath, fetchMoreResult ),
            ],
            prevResult
        )
        : prevResult
);
