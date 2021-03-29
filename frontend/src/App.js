import React, {useState, useEffect, useCallback} from 'react';
import './App.css';
import axios from "axios";
import  {useDropzone} from 'react-dropzone'


const UserProfiles = () => {

  const [UserProfiles, setUserProfiles] = useState([])
  
  const fetchUserProfiles = () => {
    axios.get("http://localhost:8080/api/v1/user-profile").then(res => {
      console.log(res)
      setUserProfiles(res.data)

    });
  }
useEffect( () => {
  fetchUserProfiles();
},[]);

return UserProfiles.map((UserProfile, index) => {

  return(
     <div key={index}>
       {UserProfile.userProfileId ? <img src = {`http://localhost:8080/api/v1/user-profile/${UserProfile.userProfileId}/image/download`} alt=""/> : null}
       <br/>
       <br/>
       <br/>
       <h1>{UserProfile.userName}</h1>
       <p>{UserProfile.userProfileId}</p>
       <MyDropzone {...UserProfile}/>
       <br/>
     </div>
     )
  
})

};

function MyDropzone({userProfileId}) {
  const onDrop = useCallback(acceptedFiles => {
    // Do something with the files
    const file = acceptedFiles[0];
    const formData = new FormData();
    formData.append("file", file);

    console.log(userProfileId)
    axios.post(`http://localhost:8080/api/v1/user-profile/${userProfileId}/image/upload`, 
    formData,{
      headers:{
        "content-Type": "multipart/form-data",
      }
    }
    ).then((res) => {
      console.log(res)
    }).catch( err => {
      console.log(err)
    })
   // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])
  const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

  return (
    <div {...getRootProps()}>
      <input {...getInputProps()} />
      {
        isDragActive ?
          <p>Drop the image here ...</p> :
          <p>Drag and  drop the profile image here, or click to select files</p>
      }
    </div>
  )
 }

function App() {
  return (
    <div className="App">
      <UserProfiles />
    </div>
  );
}

export default App;
