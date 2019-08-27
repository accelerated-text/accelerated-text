export const emptyThesaurusQuery = t => {
    
    if( t.emptyThesaurusQueryRan ) {
        return true;
    } else {
        t.emptyThesaurusQueryRan =  true;
        return t.graphqlApi.provideOnce(
            'searchThesaurus',
            {
                query:          '',
            },
            {
                data: {
                    searchThesaurus: {
                        offset:     0,
                        totalCount: 0,
                        words:      [],
                        __typename: 'ThesaurusResults',
                    },
                },
            },
        );
    }
};
