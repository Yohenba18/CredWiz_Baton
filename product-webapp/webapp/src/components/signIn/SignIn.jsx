import SignInput from "../signInput/SignInput";
import Grid from "@mui/material/Grid";
import "../../styles/signStyle.css";
const SignIn = () => {
  return (
    <>
      <div class="forms-container">
        <div class="signinpos">
          <form action="#" class="sign-in-form">
            <h2 class="title">Welcome Back!</h2>
            <Grid container spacing={1}>
              <Grid item md={3} />
              <Grid item xs={3} md={6}>
                <SignInput
                  icon="fas fa-user"
                  placeholder="Username"
                  type="email"
                />
              </Grid>
              <Grid item md={3} />
              <Grid item md={3} />
              <Grid item xs={3} md={6}>
                <SignInput
                  icon="fas fa-lock"
                  placeholder="Password"
                  type="password"
                />
              </Grid>
            </Grid>

            <input type="submit" value="Login" class="btn solid" />
          </form>
          {/* <form action="#" class="sign-up-form">
            <h2 class="title">Sign up</h2>
            <div class="input-field">
              <i class="fas fa-user"></i>
              <input type="text" placeholder="Username" />
            </div>
            <div class="input-field">
              <i class="fas fa-envelope"></i>
              <input type="email" placeholder="Email" />
            </div>
            <div class="input-field">
              <i class="fas fa-lock"></i>
              <input type="password" placeholder="Password" />
            </div>
            <input type="submit" class="btn" value="Sign up" />
            <p class="social-text">Or Sign up with social platforms</p>
            <div class="social-media">
              <a href="#" class="social-icon">
                <i class="fab fa-facebook-f"></i>
              </a>
              <a href="#" class="social-icon">
                <i class="fab fa-twitter"></i>
              </a>
              <a href="#" class="social-icon">
                <i class="fab fa-google"></i>
              </a>
              <a href="#" class="social-icon">
                <i class="fab fa-linkedin-in"></i>
              </a>
            </div>
          </form> */}
        </div>
      </div>
    </>
  );
};
export default SignIn;
