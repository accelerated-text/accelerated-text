import { shape, string }    from 'prop-types';


export default shape({
    id:                     string.isRequired,
    'isbn-13':              string.isRequired,
    thumbnail:              string.isRequired,
    title:                  string.isRequired,

    authors:                string,
    averageRating:          string,
    categories:             string,
    language:               string,
    maturityRating:         string,
    pageCount:              string,
    publishedDate:          string,
    publisher:              string,
    ratingsCount:           string,
    subtitle:               string,
});
