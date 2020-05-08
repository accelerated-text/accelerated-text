# Definition

Abstract Meaning Representations, or AMRs, are blocks in Accelerated Text that usually capture rough meaning of a sentence, inspired by Banarescu, Bonial et al. paper "[Abstract Meaning Representation for Sembanking](https://amr.isi.edu/a.pdf)".

You can find more information on AMRs in their [guidelines](https://github.com/amrisi/amr-guidelines/blob/master/amr.md).

Accelerated Text provides an AMR editor which can be accessed at [http://localhost:8080/amr/](http://localhost:8080/amr/)

# Create a new AMR block

The way we create *AMR blocks* is similar to Document plan creation. 

Notice that there is no preview of generated text, since AMRs are **abstract** - most of the time we don't fully know how text will look like, since some of the *pieces* are missing. These pieces are AMR arguments, and we use *variable blocks* to define them. 

We will recreate the same sentence as in previous section.

![create-amr](assets/first_steps/07-create-amr.gif)

# Add description

We can add description to AMR by using *Set variable block* with special name "*Description".

![add-amr-description](assets/first_steps/08-add-amr-description.gif)

# Add another frame

One of the main differences AMRs have from Document plans is that instead of *segments*, where multiple blocks can be attached, AMRs have *frames*, and each frame is another way of how a sentence with similar meaning can be expressed.

![add-amr-frame](assets/first_steps/09-add-amr-frame.gif)

# Use created AMR

Lets go back to the main Accelerated Text window in [http://localhost:8080](http://localhost:8080).

Newly created AMR will appear in the sidebar (if not, try refreshing the page).

![use-amr](assets/first_steps/10-use-amr.gif)
